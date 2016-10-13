package shashank.com.callerinfo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class EndOverlayActivity extends AppCompatActivity {

    private String number1;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.NO_GRAVITY);


        window.setBackgroundDrawableResource(R.color.colorPrimary2);
        setContentView(R.layout.endactivity_overlay);

//
        try {

//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);


            String name = getIntent().getStringExtra("name");
            number1 = getIntent().getStringExtra("number");


            TextView textView1 = (TextView) findViewById(R.id.text);

            textView1.setText(name);


            TextView textView = (TextView) findViewById(R.id.textView2);
            textView.setText(number1);


            View view = findViewById(R.id.spamStat);


        } catch (Exception e) {
            e.printStackTrace();
        }

         fab = (FloatingActionButton) findViewById(R.id.cancel);

        setListener();

    }

    public void buttonClick(View view) {

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number1));
        startActivity(intent);
        this.finish();
    }

    public void setListener() {

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                //intent.putExtra(ContactsContract.Intents.Insert.NAME, person.name);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, number1);
                startActivity(intent);
                end();



            }
        });


    }
    public void end(){
        this.finish();
    }




}
