package king.roboking;

import android.app.Application;
import android.graphics.Rect;

import java.lang.reflect.Array;

/**
 * Created by King on 2017/8/5.
 */

public class MyApplicationContext extends Application {
    private static MyApplicationContext self;
    private boolean flag=true;
    @Override
    public void onCreate() {
        super.onCreate();
        self=this;
    }
    public static MyApplicationContext getInstance(){
        return self;
    }

    private boolean getFlag(){
        return flag;
    }
    private void setFlag(boolean flag){
        this.flag=flag;
    }
}
