package com.example.customrotaryknob;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.security.acl.LastOwnerException;

import static java.lang.StrictMath.atan2;

public class RotaryKnobView extends RelativeLayout implements GestureDetector.OnGestureListener {
    private static final String TAG = "RotaryKnobView";
    private GestureDetector 	gestureDetector;
    private float 				mAngleDown , mAngleUp;
    private ImageView ivRotor;
    private Bitmap bmpRotorOn , bmpRotorOff;
    private boolean 			mState = false;
    private int					m_nWidth = 0, m_nHeight = 0;

    float lastAngle = 0;
    int BPM = 0;
    int displayValue = 20;
    int lowerBPM = 20;
    int higherBPM = 500;

    interface RotaryKnobViewListener{
        public void onStateChange(boolean newState) ;
        public void onRotate(int percentage);
    }

    private RotaryKnobViewListener m_listener;

    public void SetListener(RotaryKnobViewListener l) {
        m_listener = l;
    }

    public void SetState(boolean state) {
        mState = state;
        ivRotor.setImageBitmap(state?bmpRotorOn:bmpRotorOff);
    }

    public RotaryKnobView(Context context, int back, int rotoron, int rotoroff, final int w, final int h) {
        super(context);
        // we won't wait for our size to be calculated, we'll just store out fixed size
        m_nWidth = w;
        m_nHeight = h;
        // create stator
        ImageView ivBack = new ImageView(context);
        ivBack.setImageResource(back);
        RelativeLayout.LayoutParams lp_ivBack = new RelativeLayout.LayoutParams(
                w,h);
        lp_ivBack.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(ivBack, lp_ivBack);
        // load rotor images
        Bitmap srcon = BitmapFactory.decodeResource(context.getResources(), rotoron);
        Bitmap srcoff = BitmapFactory.decodeResource(context.getResources(), rotoroff);
        float scaleWidth = ((float) w) / srcon.getWidth();
        float scaleHeight = ((float) h) / srcon.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        bmpRotorOn = Bitmap.createBitmap(
                srcon, 0, 0,
                srcon.getWidth(),srcon.getHeight() , matrix , true);
        bmpRotorOff = Bitmap.createBitmap(
                srcoff, 0, 0,
                srcoff.getWidth(),srcoff.getHeight() , matrix , true);
        // create rotor
        ivRotor = new ImageView(context);
        ivRotor.setImageBitmap(bmpRotorOn);

        RelativeLayout.LayoutParams lp_ivKnob = new RelativeLayout.LayoutParams(w,h);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_ivKnob.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(ivRotor, lp_ivKnob);
        // set initial state
        SetState(mState);
        // enable gesture detector
        gestureDetector = new GestureDetector(getContext(), this);
    }

    private float cartesianToPolar(float x, float y) {
        return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) return true;
        else return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        float x = e.getX() / ((float) getWidth());
        float y = e.getY() / ((float) getHeight());
        mAngleDown = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.e(TAG, "onShowPress");
    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
/*        Log.e(TAG, "onSingleTapUp");
        float x = e.getX() / ((float) getWidth());
        float y = e.getY() / ((float) getHeight());
        mAngleUp = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction

        // if we click up the same place where we clicked down, it's just a button press
        if (! Float.isNaN(mAngleDown) && ! Float.isNaN(mAngleUp) && Math.abs(mAngleUp-mAngleDown) < 10) {
            SetState(!mState);
            if (m_listener != null) m_listener.onStateChange(mState);
        }*/
        return true;
    }

    public void setRotorPosAngle(float deg) {
        Matrix matrix=new Matrix();
        ivRotor.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.postRotate((float) deg * 6, m_nWidth/2, m_nHeight/2);//getWidth()/2, getHeight()/2);
        ivRotor.setImageMatrix(matrix);
    }

    public void setRotorPercentage(int percentage) {
        //int posDegree = percentage * 3 - 150;
        //if (posDegree < 0) posDegree = 360 + posDegree;
        setRotorPosAngle(percentage);
        //Log.e("==>Valuee", posDegree + "");
    }

    boolean checkSign(float number){
        if( number < 0){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float x = e2.getX() / ((float) getWidth());
        float y = e2.getY() / ((float) getHeight());
        float mainValue = 0;
        float rotDegrees = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction

        if(checkSign(rotDegrees - lastAngle)){
            //Log.e("====>>>", "+++++++++");
            if(displayValue >= lowerBPM && distanceX <= higherBPM){
                BPM++;
                if (displayValue != higherBPM){
                    displayValue++;
                }

            }
        }else {
            //Log.e("====>>>", "----------");
            if(displayValue >= lowerBPM && distanceX <= higherBPM){
                BPM--;
                if(displayValue != lowerBPM){
                    displayValue--;
                }

            }
        }

        lastAngle = rotDegrees;

        if (m_listener != null){
            if(displayValue >= lowerBPM && distanceX <= higherBPM)
                m_listener.onRotate(displayValue);
            Log.e("===>>>", displayValue + "");
        }

        if (! Float.isNaN(rotDegrees)) {
            setRotorPosAngle(BPM);
            return true; //consumed
        } else
            return false; // not consumed
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.e(TAG, "ONFling");
        return false;
    }
}
