package king.roboking;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * Created by King on 2017/8/7.
 */

public class ConnectionManager implements TransmitController {

    private Handler handler;
    private HandlerThread handlerThread;
    private Socket socket;
    private OutputStream outputStream;

    private final int C0NNECTION_BUILD=1000;
    private final int SEND_DATA=1001;
    private boolean isConnected=false;
    private boolean isSending=false;
    private OnConnectionListener onConnectionListener;
    private final String FLAG="ConnectionManager_HandlerThread";
    private final String TAG="ConnectionManager";
    public ConnectionManager(){
        handlerThread=new HandlerThread(FLAG);
        handlerThread.start();
        handler=new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch(msg.what){
                    case C0NNECTION_BUILD:
                        if(initSocket()){
                            isConnected=true;
                            Log.d(TAG,"handleMessage():连接建立成功！");
                            Toast.makeText(MyApplicationContext.getInstance(),"控制连接建立成功！",Toast.LENGTH_SHORT).show();
                            if(onConnectionListener!=null){
                                onConnectionListener.success();
                            }
                        }else{
                            isConnected=false;
                            Log.d(TAG,"handleMessage():连接建立失败！");
                            Toast.makeText(MyApplicationContext.getInstance(),"控制连接建立失败！",Toast.LENGTH_SHORT).show();
                            if(onConnectionListener!=null){
                                onConnectionListener.faild();
                            }
                        }
                        break;
                    case SEND_DATA:
                        try {
                                byte data[]=(byte[])msg.obj;
                                outputStream.write(data);
                                if(isConnected&&isSending)
                                {
                                    outputStream.flush();
                                    Log.d(TAG,"handleMessage():清除缓冲区");
                                   // Toast.makeText(MyApplicationContext.getInstance(),"数据发送:"+DataFormatTool.obtainString(data),Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        isSending=false;
                        break;
                }
            }
        };

    }
    public ConnectionManager bulid(){
        if(isConnected)
        {
            Log.d(TAG,"bulid():连接已经建立,不需要重复建立！");
        }else {
            handler.sendEmptyMessage(C0NNECTION_BUILD);
            Log.d(TAG,"bulid():发送建立连接消息");
        }
        return this;
    }
    public void disConnect(){
        if(isConnected) {
            pause();
            closeSocket();
        }
    }
    public void setOnConnectionListener(OnConnectionListener onConnectionListener){
        this.onConnectionListener=onConnectionListener;
    }
    @Override
    public void send(byte[] data) {
        sendDelayed(data,0);
        Log.d(TAG,"send()"+DataFormatTool.obtainString(data));
    }

    @Override
    public void sendDelayed(byte[] data, long time) {
        if(isConnected&&!isSending&&data!=null&&handler!=null)
        {
            isSending=true;
            if(time<0||time>30000)
            {
               time=0;
            }
            if(handler.hasMessages(SEND_DATA))
            {
                handler.removeMessages(SEND_DATA);
            }
            Message msg=handler.obtainMessage();
            msg.what=SEND_DATA;
            msg.obj=data;
            handler.sendMessageDelayed(msg,time);
            Log.d(TAG,"sendDelayed():发送数据包消息:time:"+time);
        }
    }

    public void pause(){
        isSending=false;
        byte data[];
        if(PreferenceManager.getDefaultSharedPreferences(MyApplicationContext.getInstance())
                .getBoolean("override_control_mode", false)){
            data= DataFormatTool.obtainBytes(PreferenceManager.getDefaultSharedPreferences(MyApplicationContext.getInstance())
                    .getString("control_stop",null));
        }else {
            data = new byte[8];
            data[0] = (byte) 0x55;
            data[1] = (byte) 0x00;
            data[2] = (byte) 0xff;
            data[3] = (byte) 0x00;
            data[4] = (byte) 0xff;
            data[5] = (byte) 0x00;
            data[6] = (byte) 0x00;
            data[7] = (byte) 0xaa;
        }
        send(data);
        Log.d(TAG,"执行pause()");
    }

    public void destroy(){
        pause();
        closeSocket();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            handlerThread.quitSafely();
        }
    }

    private boolean initSocket(){
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(MyApplicationContext.getInstance());
        String ip=sp.getString("ip", "none");
        int port=Integer.valueOf(sp.getString("port","-1"));

        Log.d(TAG,"initSocket():初始化Socket：ip:"+ip+" port:"+port);

        try {
            socket=new Socket(ip,port);
            //socket.setSoTimeout(10000);
            //socket.setSoLinger(true,30);
            socket.setTcpNoDelay(true);
           //socket.setSendBufferSize(8);
            socket.setKeepAlive(true);
            outputStream=socket.getOutputStream();
            return true;
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void closeSocket(){
        if(isConnected)
        {
            try {
                socket.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isConnected=false;
        }

    }
}
