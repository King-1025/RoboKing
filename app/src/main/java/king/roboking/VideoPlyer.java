package king.roboking;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;

/**
 * Created by King on 2017/8/9.
 */

public class VideoPlyer implements MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener{

    private Context context;
    private MediaPlayer mp;
    private int mVideoWidth;
    private int mVideoHeight;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private SurfaceHolder sh;
    private boolean isPrepareAsync;
    private Bitmap bitmap;
    private final String TAG="VideoPlayer";
    public VideoPlyer(Context context,SurfaceHolder surfaceHolder,boolean isPrepareAsync){
        this.context=context;
        mp=new MediaPlayer(context);
        sh=surfaceHolder;
        this.isPrepareAsync=isPrepareAsync;
        mp.setOnBufferingUpdateListener(this);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setOnVideoSizeChangedListener(this);
    }

    public void start(String url){
        play(url);
        //new SelfTask().execute("play");
    }

    public void destroy(){
        releaseMediaPlayer();
        //new SelfTask().execute("releaseMediaPlayer");
        doCleanUp();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void screenshot(){
        if(mp==null){
            Log.d(TAG,"doScreenshot():视频初始化未完成，无法截图!");
            Toast.makeText(context,"视频初始化未完成，无法截图!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(mp.isBuffering())
        {
            Log.d(TAG,"doScreenshot():视频正在缓冲中,暂时不能截图!");
            Toast.makeText(context,"视频正在缓冲中,暂时不能截图!",Toast.LENGTH_SHORT).show();
            return;
        }
        bitmap=mp.getCurrentFrame();
        doScreenshot(bitmap);
    }
    public int getmVideoWidth(){
        int width=0;
        if(mp!=null){
            width=mp.getVideoWidth();
        }
        return width;
    }
    public int getmVideoHeight(){
        int height=0;
        if(mp!=null){
            mp.getVideoHeight();
        }
        return height;
    }
    //播放器相关方法
    private void play(String video_url){
        doCleanUp();
        if(video_url==null|video_url=="")
        {
            Log.d(TAG,"play():视频源路径为空");
            return;
        }
        try {
            Log.d(TAG," play():video_url:"+video_url);
            mp.setDataSource(video_url);
            mp.setDisplay(sh);
            String quality=PreferenceManager.getDefaultSharedPreferences(MyApplicationContext.getInstance())
                    .getString("list_video_quality_show","1");
            int value=-16;
            if(quality.equals("1")){
                value=-16;
            }else if(quality.equals("2")){
                value=0;
            }else if(quality.equals("3")){
                value=16;
            }
            mp.setVideoQuality(value);
            if(isPrepareAsync)
            {
                mp.prepareAsync();
            }
            else{
                mp.prepare();
            }
            Log.d(TAG,"play():isPrepareAsync:"+isPrepareAsync);
           // setVolumeControlStream(AudioManager.STREAM_MUSIC);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"play():错误");
        }
    }
    private void releaseMediaPlayer() {
        if (mp!= null)
        {
            mp.release();
            mp= null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        sh.setFixedSize(mVideoWidth, mVideoHeight);
        mp.start();
        Log.d(TAG,"执行startVideoPlayback()");
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
            //new SelfTask().execute("startVideoPlayback");
        }
        Log.d(TAG, "执行onPrepared()");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(!mp.isPlaying()){
            Log.d(TAG,"onCompletion():播放器停止工作！");
            Toast.makeText(context,"播放器停止工作！",Toast.LENGTH_LONG).show();
        }
        Log.d(TAG,"执行onCompletion():mp:"+mp);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.d(TAG,"onBufferingUpdate():视频缓冲:"+percent+"%");
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        if (width == 0 || height == 0) {
            Log.e(TAG, "onVideoSizeChanged():无效视频:width(" + width + ") or height(" + height + ")");
        }else {
            mIsVideoSizeKnown = true;
            mVideoWidth = width;
            mVideoHeight = height;
            if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
                startVideoPlayback();
                //new SelfTask().execute("startVideoPlayback");
            }
        }
        Log.v(TAG, "执行onVideoSizeChanged()");
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.d(TAG,"onInfo():what:"+what);
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(context,"播放器出错！"+what,Toast.LENGTH_LONG).show();
        Log.d("","onError():what:"+what);
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void doScreenshot(Bitmap bitmap){

        if(bitmap==null){
            Log.d(TAG,"doScreenshot():无法获取当前图片帧!");
            Toast.makeText(context,"无法获取当前图片帧!",Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG,"doScreenshot():开始截图...");

        String path = PreferenceManager.getDefaultSharedPreferences(MyApplicationContext.getInstance())
                .getString("screenshot_save_path","/sdcard/"+context.getPackageName()+"/截图");
        String mode=PreferenceManager.getDefaultSharedPreferences(MyApplicationContext.getInstance())
                .getString("list_screenshot_show","1");
        Bitmap.CompressFormat cf=Bitmap.CompressFormat.JPEG;
        if(mode.equals("1")){
            cf=Bitmap.CompressFormat.JPEG;
            mode=".jpeg";
        }else if(mode.equals("2")){
            cf=Bitmap.CompressFormat.PNG;
            mode=".png";
        }else if(mode.equals("3")){
            cf=Bitmap.CompressFormat.WEBP;
            mode=".webp";
        }
        SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日EHH时mm分ss秒");
        String body=format.format(new Date());
        String fullPath=path+"/"+body+mode;
        Log.d(TAG,"doScreenshot():截图完整储存路径:fullPath:"+fullPath);
        Toast.makeText(context,fullPath,Toast.LENGTH_SHORT).show();

        try {
            File file=new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
            file=new File(fullPath);
            file.createNewFile();
            FileOutputStream out=new FileOutputStream(file);

            if(bitmap.compress(cf,60,out))
            {
                out.flush();
                out.close();
                Toast.makeText(context,"截图成功!"+fullPath,Toast.LENGTH_SHORT).show();
                return;
            }else{
                Log.w(TAG,"doScreenshot():图片格式化失败!");
                Toast.makeText(context,"图片格式化失败!",Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context,"文件没有发现",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,"文件写入异常",Toast.LENGTH_SHORT).show();
        }
       // Toast.makeText(context,"截图失败，请重试！",Toast.LENGTH_SHORT).show();
    }

}
