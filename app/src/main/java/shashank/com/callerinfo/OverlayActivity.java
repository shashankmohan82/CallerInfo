package shashank.com.callerinfo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import static shashank.com.callerinfo.R.color.colorText;

public class OverlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.TOP);


        window.setBackgroundDrawableResource(R.color.colorPrimary);
        setContentView(R.layout.activity_overlay);

//        WindowManager.LayoutParams layoutParams = window.getAttributes();
//        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
//
//        final RelativeLayout rl = (RelativeLayout)findViewById(R.id.ly1);
//
//        rl.bringToFront();
//        rl.setOnTouchListener(new View.OnTouchListener()
//        {
//            @Override
//            public boolean onTouch(View v, MotionEvent event)
//            {
//                double X = event.getX();
//                double Y = event.getY();
//                String.valueOf(event.getX() + "x" + String.valueOf(event.getY()));
//
//                rl.bringToFront();
//                rl.setX((float) X);
//                rl.setY((float) Y);
//                return false;
//            }
//        });
//
//        window.setAttributes(layoutParams);

        try{

//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

            String number = getIntent().getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            String state = getIntent().getStringExtra(TelephonyManager.EXTRA_STATE);
            CreateList cl = new CreateList();
            cl.addItems();
            if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                Intent intent = new Intent(this,EndOverlayActivity.class);
                //intent.putExtra("name",cl.fetchName(number));
                intent.putExtra("number",number);
                intent.putExtra("name",cl.fetchName(number));
                startActivity(intent);
                this.finish();
            }



            TextView textView2 = (TextView) findViewById(R.id.text);




            TextView textView = (TextView) findViewById(R.id.textView2);
            TextView textView3 = (TextView) findViewById(R.id.textView3);
            textView.setText(number);

                textView2.setText(cl.fetchName(number));
                textView3.setText(cl.fetchOperator(number));
                View view = findViewById(R.id.spamStat);
            ImageView imageView = (ImageView) findViewById(R.id.imageView2);

                if(cl.isSpam(number)){

                   imageView.setImageResource(R.drawable.tag_green);
                }
                else {
                    imageView.setImageResource(R.drawable.tag_green);
                }



        }
        catch(Exception e){
            e.printStackTrace();
        }



    }

    public void buttonClick(View view) {
        this.finish();
    }
}
