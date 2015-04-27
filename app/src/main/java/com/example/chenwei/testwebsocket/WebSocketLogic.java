package com.example.chenwei.testwebsocket;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.codebutler.android_websockets.WebSocketClient;

import org.apache.http.impl.cookie.BasicMaxAgeHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * websocket 单件
 *
 * Created by chenwei on 4/22/15.
 */
public class WebSocketLogic {

    private final String TAG = "chenwei.WebSocketLogic";
    /** 服务器地址 */
//    private static final String SREVER_URL="10.61.137.26:8888";192.168.1.104
    private static final String SREVER_URL="192.168.1.104:8888";
    public static final int MSG_CONNECT_OK = 1;
    public static final int MSG_CONNECT_FAIL = 2;
    public static final int MSG_DISCONNECT = 3;
    /** 服务器通知*/
    public static final int MSG_SYSTEM_NOTIFY = 4;
    /** 显示聊天内容 */
    public static final int MSG_SHOW_CHAT_CONTENT = 5;

    private static WebSocketLogic instance = null;

    public static WebSocketLogic getInstance(){
        if(instance == null){
            instance = new WebSocketLogic();
        }
        return instance;
    }

    /**
     * 构造方法
     */
    private WebSocketLogic(){

    }

    private String peopeleId ;
    private String roomName;
    private int count ;

    private List<Handler> mHandlers = new ArrayList<Handler>();
//    private Handler mHandler  = null;



    public void addHandler(Handler handler){

        if(!mHandlers.contains(handler)){
            mHandlers.add(handler);
        }
    }

    public void removeHandler(Handler handler){
        if(mHandlers.contains(handler)){
            mHandlers.remove(handler);
        }
    }

    private WebSocketClient client = null;

    /**
     * websocket 连接
     */
    public void connect(){

        Log.i(TAG, "connect()");

        if(client != null && client.isConnected()){
            return;
        }

        List<BasicNameValuePair> extraHeaders = Arrays.asList(
                new BasicNameValuePair("Cookie", "session=abcd")
        );

        client = new WebSocketClient(URI.create("ws://" + SREVER_URL + "/websocket"),new WebSocketClient.Listener(){

            @Override
            public void onConnect() {
                Log.i(TAG, "Connected!");
                notifyHandlers(MSG_CONNECT_OK,"");
            }

            @Override
            public void onMessage(String message) {
                Log.i(TAG, "onMessage() message = "+message);
                try {
                    JSONObject json = new JSONObject(message);
                    Log.i(TAG,"message  json = "+json.toString());

                    String tmp_type = json.getString("type");
                    if(tmp_type.equals("1")){   //进入聊天室成功
                        String tmp = json.getString("id");
                        setPeopeleId(tmp);

                        tmp = json.getString("room_name");
                        setRoomName(tmp);

                        tmp = json.getString("count");
                        setCount(Integer.parseInt(tmp));

                    } else if(tmp_type.equals("2")){    //系统通知

                        String tmp = json.getString("count");
                        setCount(Integer.parseInt(tmp));

                        tmp = json.getString("msg");

                        notifyHandlers(MSG_SYSTEM_NOTIFY,tmp);

                    } else if(tmp_type.equals("3")){   //显示聊天内容
                        String fromid = json.getString("id");
                        String content = json.getString("msg");

                        notifyHandlers(MSG_SHOW_CHAT_CONTENT,fromid+" : "+content);
                    }

                } catch (JSONException e) {
                    Log.e(TAG,e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(byte[] data) {
                Log.i(TAG, "onMessage(byte[])  "+new String(data).toString());
            }

            @Override
            public void onDisconnect(int code, String reason) {

                notifyHandlers(MSG_DISCONNECT,"code=["+code+"], reason="+reason);
                disconnect();

                Log.i(TAG,"onDisconnect() code = "+code + " , reason="+reason);
            }



            @Override
            public void onError(Exception error) {
                Log.e(TAG, "onError  = "+error.toString()+" ["+error+"]");

//                java.net.SocketException: recvfrom failed: ECONNRESET (Connection reset by peer)

//                if(error instanceof NullPointerException){
//                    Log.i(TAG,"null");
//                }

                if(error instanceof SocketException){
                    if(error.toString().contains("Connection reset by peer")){
                        notifyHandlers(MSG_CONNECT_FAIL,error.toString());
                    }
                }

                disconnect();
            }
        },extraHeaders);

        client.connect();
    }

    /**
     * 判断是否连接
     * @return
     */
    public boolean isConnected(){
        if(client!=null && client.isConnected()){
            return true;
        }
        return false;
    }

    Message msg;



    /**
     *
     */
    private void notifyHandlers(int what, String str){

        Handler tmp ;

        for(int i=0;i<mHandlers.size();i++){
            tmp = mHandlers.get(i);
            msg = tmp.obtainMessage(what,str);
            tmp.sendMessage(msg);
        }
    }

    /**
     * websocket 断开
     */
    public void disconnect(){
        Log.i(TAG,"disconnect()");
        if(client != null && client.isConnected()){
            client.disconnect();
            Log.i(TAG,"已断开");
        }

        client = null;
    }

    /**
     * 向服务器发送msg
     * @param str
     */
    public void sendMsg(String str){
        Log.i(TAG,"sendMsg()");
        if(client != null && client.isConnected() && !TextUtils.isEmpty(str)){
            Log.i(TAG,"连接状态");
//            client.send("hello!");
//            client.send(new String("hello world by wei.chen").getBytes());
              client.send(str);
        }
    }

    /**
     * TODO
     */
    public void ping(){

    }

    //------------------get/set------------------------------------------------
    public String getPeopeleId() {
        return peopeleId;
    }

    public void setPeopeleId(String peopeleId) {
        this.peopeleId = peopeleId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    /**
     * 单件销毁
     */
    public static void onDestory(){
        if(instance != null){

            instance.disconnect();
            instance.client = null;

            instance = null;
        }
    }
}
