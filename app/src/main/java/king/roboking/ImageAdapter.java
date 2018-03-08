package king.roboking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2017/8/13.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> pathList;
    public ImageAdapter(Context context, ArrayList<String> list){
        if(list!=null){
            this.context=context;
            pathList=list;
        }
    }
    @Override
    public int getCount() {
        int size=0;
        if(pathList!=null){
            size=pathList.size();
        }
        return size;
    }

    @Override
    public Object getItem(int position) {
        return pathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if(convertView!=null) {
            imageView = (ImageView) convertView;
        }else{
            imageView=new ImageView(context);
        }
        Bitmap bitmap=null;
        bitmap= BitmapFactory.decodeFile(pathList.get(position));
        int width = bitmap.getWidth() / 3;
        int height = bitmap.getHeight() / 3;
        imageView.setImageBitmap(bitmap);
        imageView.setBackgroundColor(Color.RED);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new Gallery.LayoutParams(width,height));
        return imageView;
    }
}
