package king.roboking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import io.vov.vitamio.Vitamio;
import king.roboking.View.CircularRod;

public class MainActivity extends AppCompatActivity implements  SurfaceHolder.Callback {

    private Context context;
    private SurfaceView sf;
    private SurfaceHolder sh;

    private VideoPlyer videoPlyer;
    private String videoPath;

    private Toolbar toolbar;
    private int statusBarHeight;
    private int titileBarHeight;

    private LayoutInflater layoutInflater;
    private CircularRod circularRod;
    private View videoParent;
    private TextView tv;
    private ConnectionManager connectionManager;
    private FloatWindowManager floatWindowManager;

    private MenuItem control;
    private Handler handler;
    private final String TAG="MainActivity";
    private final int CONNECT_SUCCESS=100;
    private final int CONNECT_FAILD=101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        //Vitamio初始化(必须在整体布局初始化之前)
        Vitamio.isInitialized(context);
        //初始化整体布局
        setContentView(R.layout.main);
        //SurfaceView和SurfaceHolder初始化
        sf = (SurfaceView) findViewById(R.id.video);
        sh = sf.getHolder();
        sh.addCallback(this);
        sh.setFormat(PixelFormat.RGBA_8888);
        sh.setKeepScreenOn(true);
        //初始化工具栏
        toolbar= (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.matte_48px));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //初始化状态栏和标题栏高度
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        statusBarHeight = rect.top;
        titileBarHeight = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop() - statusBarHeight;
         //初始化连接管理者
        connectionManager = new ConnectionManager();
        connectionManager.setOnConnectionListener(new OnConnectionListener() {
            @Override
            public void success() {
                handler.sendEmptyMessage(CONNECT_SUCCESS);
            }

            @Override
            public void faild() {
                handler.sendEmptyMessage(CONNECT_FAILD);
            }
        });
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case CONNECT_SUCCESS:
                        //显示摇杆控制器
                        floatWindowManager.show(circularRod);
                        control.setTitle("断开") ;
                        tv.setTextColor(Color.CYAN);
                        tv.setText("控制连接已经成功创建！");
                        control.setEnabled(true);
                        break;
                    case CONNECT_FAILD:
                        tv.setTextColor(Color.RED);
                        tv.setText("控制连接创建失败！");
                        control.setEnabled(true);
                        break;
                }
            }
        };
        //初始化摇杆控制器
        layoutInflater = LayoutInflater.from(this);
        circularRod = (CircularRod) layoutInflater.inflate(R.layout.circular_rod, null);
        circularRod.setTransmitController(connectionManager);
        tv= (TextView) findViewById(R.id.data_info);
        circularRod.setOnDataInfoListener(new OnDataInfoListener(){
            @Override
            public void publish(String info) {
                if(tv!=null)
                {
//                    if(tv.getText().length()>100) {
//                        tv.setText(info);
//                    } else{
//                        tv.append(info+"\n");
//                    }
                    tv.setText(info);
                }
                else{
                    Log.d(TAG,"publish():tv:"+tv);
                }
                Log.d(TAG,"publish():info:"+info);
            }
        });
        floatWindowManager=new FloatWindowManager(this);
        videoParent=findViewById(R.id.video_parent);
        videoParent.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               switch (event.getAction()) {
                   case MotionEvent.ACTION_DOWN:
                       floatWindowManager.changePosition(event.getRawX(),event.getRawY());
                       break;
                   case MotionEvent.ACTION_UP:
                       Log.d(TAG,"onTouch():sf:离开屏幕");
                       break;
               }
               return false;
           }
        });
        int width=floatWindowManager.getWindowWidth();
        int height= floatWindowManager.getWindowHeight()-statusBarHeight-titileBarHeight;
        ViewGroup.LayoutParams lp= sf.getLayoutParams();
        String videoSize=PreferenceManager.getDefaultSharedPreferences(MyApplicationContext.getInstance())
                .getString("list_video_show","1");
        if(videoSize.equals("1")){
            Log.d(TAG,"原始大小");
        }else if(videoSize.equals("2")){
            lp.width=width/2;
            lp.height=height/2;
            sh.setFixedSize(width/2,height/2);
        }else if(videoSize.equals("3")){
            lp.width=width;
            lp.height=height;
            sh.setFixedSize(width,height);
        }
        Log.d(TAG,"父控件:videoParent:width:"+width+" height:"+height);
        //初始化播放器
        videoPlyer=new VideoPlyer(this,sh,true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch(id){
            case R.id.screenshot:
                videoPlyer.screenshot();
                Log.d(TAG,"onOptionsItemSelected():截图");
                break;
            case R.id.look_photo:
                startActivity(new Intent(this,ImageSeeActivity.class));
                finish();
                Log.d(TAG,"onOptionsItemSelected():查看");
                break;
            case R.id.control:
                control = item;
                if(item.getTitle().equals("控制")) {
                    connectionManager.bulid();
                    control.setEnabled(false);
                    tv.setTextColor(Color.GREEN);
                    tv.setText("正在创建控制连接...");
                }else if(item.getTitle().equals("断开")){
                    connectionManager.disConnect();
                    floatWindowManager.hide();
                    control.setTitle("控制") ;
                    tv.setText(null);
                }else{
                    tv.setTextColor(Color.RED);
                    tv.setText("控制连接异常,请重启程序！");
                    Log.d(TAG,"异常");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
       // kkk
       // floatWindowManager.show(circularRod);
        Log.d(TAG,"执行onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG,"执行onRestart()");
    }

    @Override
    protected void onPause() {
        connectionManager.pause();
        floatWindowManager.hide();
        super.onPause();
        Log.d(TAG,"执行onPause()");
    }

    @Override
    protected void onDestroy() {
        videoPlyer.destroy();
        connectionManager.destroy();
        super.onDestroy();
        Log.d(TAG,"执行onDestroy()");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //获取视频源路径
        videoPath = PreferenceManager.getDefaultSharedPreferences(MyApplicationContext.getInstance())
                .getString("video_url",null);
        Log.d(TAG,"videoPath:"+videoPath);
        //显示视频源到工具栏的副标题
        toolbar.setSubtitle(videoPath);
        videoPlyer.start(videoPath);
        Log.d(TAG,"surfaceCreated():视频播放器工作");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG,"执行surfaceChanged()");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        videoPlyer.destroy();
        Log.d(TAG,"执行surfaceDestroyed()");
    }

}
