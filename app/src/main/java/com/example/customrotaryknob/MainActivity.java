package com.example.customrotaryknob;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    Singleton m_Inst = Singleton.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Scaling mechanism, as explained on:
        // http://www.pocketmagic.net/2013/04/how-to-scale-an-android-ui-on-multiple-screens/
        m_Inst.InitGUIFrame(this);

        RelativeLayout panel = new RelativeLayout(this);
        setContentView(panel);

        final TextView tv = new TextView(this);
       // tv.setText("Rotary knob control\nRadu Motisan 2013\nwww.pocketmagic.net");
        tv.setText("0");
        tv.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        panel.addView(tv, lp);

        final TextView tv2 = new TextView(this); tv2.setText("");
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        panel.addView(tv2, lp);


        RotaryKnobView rv = new RotaryKnobView(this, R.drawable.stator, R.drawable.rotoron, R.drawable.rotoroff,
                m_Inst.Scale(300), m_Inst.Scale(300));
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        panel.addView(rv, lp);
        //Knob initial position
        rv.setRotorPercentage(0);
        rv.SetListener(new RotaryKnobView.RotaryKnobViewListener() {
            public void onStateChange(boolean newstate) {
                Toast.makeText(MainActivity.this,  "New state:"+newstate,  Toast.LENGTH_SHORT).show();
            }

            public void onRotate(final int percentage) {
                tv2.post(new Runnable() {
                    public void run() {
                        tv2.setText("\n" + percentage + "%\n");
                    }
                });
            }
        });


    }

}