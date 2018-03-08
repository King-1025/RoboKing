package king.roboking;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity implements View.OnClickListener {

    private Button control;
    private Button setting;
    private Button about;
    private Drawable c_0;
    private Drawable c_1;
    private Drawable c_2;
    private Handler hd;
    private final static int STATE_0=100;
    private final static int STATE_1=101;
    private final static int STATE_2=102;
    private long delayedTine=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        control= (Button) findViewById(R.id.control);
        setting= (Button) findViewById(R.id.setting);
        about= (Button) findViewById(R.id.about);
        control.setOnClickListener(this);
        setting.setOnClickListener(this);
        about.setOnClickListener(this);
        c_0=control.getBackground();
        c_1=setting.getBackground();
        c_2=about.getBackground();
        hd=new Handler(){
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what)
                {
                    case STATE_0:
                        change(c_2,c_0,c_1);
                        hd.sendEmptyMessageDelayed(STATE_1,delayedTine);
                        break;
                    case STATE_1:
                        change(c_1,c_2,c_0);
                        hd.sendEmptyMessageDelayed(STATE_2,delayedTine);
                        break;
                    case STATE_2:
                        change(c_0,c_1,c_2);
                        hd.sendEmptyMessageDelayed(STATE_0,delayedTine);
                        break;
                }
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        hd.sendEmptyMessageDelayed(STATE_0,1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        close();
    }
    private void close(){
        hd.removeMessages(STATE_0);
        hd.removeMessages(STATE_1);
        hd.removeMessages(STATE_2);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void change(Drawable p0, Drawable p1, Drawable p2){
        control.setBackground(p0);
        setting.setBackground(p1);
        about.setBackground(p2);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        close();
        Drawable dr=v.getBackground();
        change(dr,dr,dr);
        switch(v.getId())
        {
            case R.id.control:
                AnimationTool.styleGoActivity(this,MainActivity.class,1);
                break;
            case R.id.setting:
                AnimationTool.styleGoActivity(this,SettingsActivity.class,1);
                break;
            case R.id.about:
                new AlertDialog.Builder(this).setMessage(getResources().getString(R.string.about_details))
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                hd.sendEmptyMessageDelayed(STATE_0,1000);
                            }
                        }).show();
                break;
        }
    }

}
