package king.roboking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;

/**
 * Created by King on 2017/8/13.
 */

public class AnimationTool {
    public static  void styleGoActivity(Activity ctx, Class<?> cls1, int type){
        ctx.startActivity(new Intent(ctx,cls1));
        switch(type){
            case 0:
                ctx.overridePendingTransition(R.anim.from_activity,R.anim.to_activity);
                break;
            case 1:
                break;
        }

    }
}
