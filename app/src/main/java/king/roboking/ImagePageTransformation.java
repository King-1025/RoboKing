package king.roboking;

import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

/**
 * Created by King on 2017/8/11.
 */

public class ImagePageTransformation implements PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        float centerX=page.getWidth()/2;
        float centerY=page.getHeight()/2;
        float scaleFactor=Math.max(0.85f,1-Math.abs(position));
        float rotate=20*Math.abs(position);
        if(position<-1){

        }else if(position<0){
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(rotate);
        }else if(position>=0&&position<1){
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(-rotate);
        }else{
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setRotationY(-rotate);
        }
    }
}
