package king.roboking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by King on 2017/8/11.
 */

public class ImagePagerAdapter extends PagerAdapter {
    private Context context;
    private List<String>pathList;
    private int size;
    private final String TAG="SelfPagerAdapter";
    public ImagePagerAdapter(Context context,List<String>list){
        if(list!=null){
            this.context=context;
            pathList=list;
            size=list.size();
        }else{
            Log.d(TAG,"listImageView is null!");
        }
    }

    @Override
    public int getCount() {
        return size;
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
           return false;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView=new ImageView(context);
        Bitmap bitmap= BitmapFactory.decodeFile(pathList.get(position));
        imageView.setImageBitmap(bitmap);
        container.addView(imageView,position);
        return imageView;
    }

    private Bitmap doStyleImage(Bitmap sourceBitmap){

        if(sourceBitmap==null){
            Log.d(TAG,"StyleImage():sourceBitmap为空,无法风格化！");
            return null;
        }
        //绘制原图的下一半图片
        Matrix matrix=new Matrix();
        //倒影翻转
        matrix.setScale(1,-1);
        Bitmap inverseBitmap=Bitmap.createBitmap(sourceBitmap,0,sourceBitmap.getHeight()/2,sourceBitmap.getWidth(),sourceBitmap.getHeight()/3,matrix,false);
        //合成图片
        Bitmap groupbBitmap=Bitmap.createBitmap(sourceBitmap.getWidth(),sourceBitmap.getHeight()+sourceBitmap.getHeight()/3+60,sourceBitmap.getConfig());
        //以合成图片为画布
        Canvas gCanvas=new Canvas(groupbBitmap);
        //将原图和倒影图片画在合成图片上
        gCanvas.drawBitmap(sourceBitmap,0,0,null);
        gCanvas.drawBitmap(inverseBitmap,0,sourceBitmap.getHeight()+50,null);
        //添加遮罩
        Paint paint=new Paint();
        Shader.TileMode tileMode= Shader.TileMode.CLAMP;
        LinearGradient shader=new LinearGradient(0,sourceBitmap.getHeight()+50,0,
                groupbBitmap.getHeight(), Color.BLACK,Color.TRANSPARENT,tileMode);
        paint.setShader(shader);
        //这里取矩形渐变区和图片的交集
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        gCanvas.drawRect(0,sourceBitmap.getHeight()+50,sourceBitmap.getWidth(),groupbBitmap.getHeight(),paint);
        return groupbBitmap;
    }

    /*
     imageParent=findViewById(R.id.image_parent);
        viewPager= (ViewPager) findViewById(R.id.view_pager);
        imageParent.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 return viewPager.dispatchTouchEvent(event);
             }
         });
        tv= (TextView) findViewById(R.id.path_show);
        pathList=getImagePathList();
        //viewPager.setOffscreenPageLimit(3);
//        ViewGroup.LayoutParams lp=viewPager.getLayoutParams();
//        pageWidth=(int)(getResources().getDisplayMetrics().widthPixels*3.0f/5.0f);
//        if(lp==null){
//            lp=new ViewGroup.LayoutParams(pageWidth,ViewGroup.LayoutParams.MATCH_PARENT);
//        }else{
//            lp.width=pageWidth;
//        }
//        viewPager.setLayoutParams(lp);
//        viewPager.setPageMargin(-200);
        viewPager.setPageTransformer(true,new ImagePageTransformation());
        viewPager.setAdapter(new ImagePagerAdapter(this,
                pathList));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
           @Override
           public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

           }

           @Override
           public void onPageSelected(int position) {
                tv.setText(pathList.get(position));
           }

           @Override
           public void onPageScrollStateChanged(int state) {

           }
       });
     */
}

