package king.roboking;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationTool.styleGoActivity(SplashActivity.this,StartActivity.class,1);
                finish();
            }
        },2500);
    }
}
