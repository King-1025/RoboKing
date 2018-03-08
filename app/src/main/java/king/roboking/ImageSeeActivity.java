package king.roboking;

import android.app.Activity;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageSeeActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener,ViewSwitcher.ViewFactory{

    private ImageSwitcher imageSwitcher;
    private Gallery gallery;
    private ImageAdapter imageAdapter;
    private Button back;
    private Button delete;
    private Button control;
    private TextView tvCount;
    private TextView tvPath;
    private ArrayList<String>pathList;
    private View oldView;
    private int selectedPosition=-1;
    private final String TAG="ImageSeeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_see2);
        imageSwitcher= (ImageSwitcher) findViewById(R.id.image_switcher);
        gallery= (Gallery) findViewById(R.id.gallery);
        back= (Button) findViewById(R.id.back);
        delete= (Button) findViewById(R.id.delete);
        delete.setEnabled(false);
        control= (Button) findViewById(R.id.to_control);
        back.setOnClickListener(this);
        delete.setOnClickListener(this);
        control.setOnClickListener(this);
        tvCount= (TextView) findViewById(R.id.count);
        tvCount.setText("无截图！");
        tvPath= (TextView) findViewById(R.id.image_info);
        imageSwitcher.setFactory(this);
        gallery.setOnItemSelectedListener(this);
        pathList=getImagePathList();
        gallery.setSpacing(20);
        imageAdapter=new ImageAdapter(this,pathList);
        gallery.setAdapter(imageAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(pathList!=null&&pathList.size()>0){
            String str=pathList.get(position);
            imageSwitcher.setImageURI(Uri.parse(str));
            tvCount.setText(pathList.size()+"/"+(position+1));
            tvPath.setText(str);
            if(oldView!=null){
                oldView.setPadding(0,0,0,0);
            }
            view.setPadding(5,0,5,0);
            oldView=view;
            selectedPosition=position;
            delete.setEnabled(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public View makeView() {
        ImageView imageView=new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ViewSwitcher.LayoutParams lp= (ViewSwitcher.LayoutParams) imageView.getLayoutParams();
        if(lp==null){
            lp=new ViewSwitcher.LayoutParams(ViewSwitcher.LayoutParams.MATCH_PARENT,ViewSwitcher.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lp);
        }else {
            lp.width = ViewSwitcher.LayoutParams.MATCH_PARENT;
            lp.height = ViewSwitcher.LayoutParams.MATCH_PARENT;
        }
        return imageView;
    }
    private ArrayList<String> getImagePathList(){
        ArrayList<String> list=null;
        String path= PreferenceManager.getDefaultSharedPreferences(MyApplicationContext.getInstance())
                .getString("screenshot_save_path",null);
        if(path==null){
            Log.d(TAG,"getImages():screenshot_save_path is null！");
            return null;
        }
        File fd=new File(path);
        if(fd==null){
            Log.d(TAG,"getImages():图片保存目录无效！");
            return null;
        }
        File[] files=fd.listFiles();
        if(files==null||files.length==0){
            Log.d(TAG,"getImages():files无效！");
            return null;
        }
        list=new ArrayList<>();
        for(int i=0;i<files.length;i++)
        {
            File targetFile=files[i];
            String targetFilePath=targetFile.getPath();
            String str=targetFilePath.substring(targetFilePath.lastIndexOf(".")+1,targetFilePath.length()).toLowerCase();
            if(str.equals("jpeg")||str.equals("png")||str.equals("webp")){
                list.add(targetFilePath);
            }
        }
        return list;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
            case R.id.delete:
                if(selectedPosition>=0&&selectedPosition<pathList.size()) {
                    delete.setEnabled(false);
                    String path = pathList.get(selectedPosition);
                    File f = new File(path);
                    if (f.exists()) {
                        f.delete();
                    }
                    pathList.remove(selectedPosition);
                    selectedPosition=-1;
                    imageAdapter.notifyDataSetChanged();
                    if(pathList.size()<1){
                        delete.setEnabled(false);
                        imageSwitcher.setImageURI(null);
                        tvCount.setText("无截图！");
                        tvPath.setText(null);
                    }else if(pathList.size()==1){
                        String str=pathList.get(0);
                        imageSwitcher.setImageURI(Uri.parse(str));
                        tvCount.setText(pathList.size()+"/"+1);
                        tvPath.setText(str);
                        selectedPosition=0;
                        delete.setEnabled(true);
                    }else{
                        delete.setEnabled(true);
                        imageSwitcher.setImageURI(null);
                        tvCount.setText("选择图片");
                        tvPath.setText(null);
                    }
                    Toast.makeText(this,path+"已经被删除！",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"请选中你想所删除的图片。",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.to_control:
                AnimationTool.styleGoActivity(this,MainActivity.class,1);
                finish();
                break;
        }
    }
}
