package com.example.chenwei.testwebsocket;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codebutler.android_websockets.WebSocketClient;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 测试 websocket 功能
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    private final String TAG = "chenwei.TestWebSocket";

    private Button mBtConnect,mBtDisConn,mBtSendMsg;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            int what = -1;
            what = msg.what;
            if(what == WebSocketLogic.MSG_CONNECT_OK){

                Toast.makeText(MainActivity.this,"连接服务器成功，正在进入聊天室....",Toast.LENGTH_SHORT).show();

                startActivity(new Intent(MainActivity.this,ChatRoomActivity.class));

            } else if(what == WebSocketLogic.MSG_CONNECT_FAIL){

                String tmp = (String) msg.obj;
                Toast.makeText(MainActivity.this,"连接服务器失败，请重新尝试！["+tmp+"]",Toast.LENGTH_SHORT).show();

            } else if(what == WebSocketLogic.MSG_DISCONNECT){
            } else if(what == WebSocketLogic.MSG_SYSTEM_NOTIFY){
                String tmp = (String) msg.obj;
                Toast.makeText(MainActivity.this,tmp,Toast.LENGTH_SHORT).show();
            }
//            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtConnect = (Button) this.findViewById(R.id.bt_websocket_open);
        mBtConnect.setOnClickListener(this);

        mBtDisConn = (Button) this.findViewById(R.id.bt_websocket_close);
        mBtDisConn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        WebSocketLogic.getInstance().addHandler(mHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WebSocketLogic.getInstance().removeHandler(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar willon
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        WebSocketLogic.getInstance().disconnect();
    }

    /**
     * 测试　注册接口
     */
    private void testRegister(){

        Log.i(TAG,"testRegister() ");
        String Server_url="http://192.168.1.104:8888/register";
        URL url = null;
        HttpURLConnection con = null;
        OutputStream output = null;
        InputStream in = null;

        try {
            url = new URL(Server_url);
            Log.i(TAG,"url="+url.toString());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(10000);

            JSONObject json=new JSONObject();
            try {
                json.put("username","bb");
                json.put("pwd",md5("123456"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            output = con.getOutputStream();
            output.write(json.toString().getBytes());

            Log.i(TAG, "注册：　" + json.toString());

            readStream(con.getInputStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        } catch (IOException e) {
            Log.e(TAG,e.toString());
            e.printStackTrace();
        }  finally {
            if(con != null) con.disconnect();
            if(output != null) try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试　登陆接口
     */
    private void testLogin(){

        Log.i(TAG,"testLogin() ");
        String Server_url="http://192.168.1.104:8888/login";
        URL url = null;
        HttpURLConnection con = null;
        OutputStream output = null;
        InputStream in = null;

        try {
            url = new URL(Server_url);
            Log.i(TAG,"url="+url.toString());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(10000);

            JSONObject json=new JSONObject();
            try {
                json.put("username","bb");
                json.put("pwd",md5("12345"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            output = con.getOutputStream();
            output.write(json.toString().getBytes());


            Log.i(TAG, "登陆：　" + json.toString());

            readStream(con.getInputStream());

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
        } catch (IOException e) {
            Log.e(TAG,e.toString());
            e.printStackTrace();
        }  finally {
            if(con != null) con.disconnect();
            if(output != null) try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {
        if(mBtConnect == v){
//            if(WebSocketLogic.getInstance().isConnected()){
//                Toast.makeText(MainActivity.this,"回到聊天室....",Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(MainActivity.this,ChatRoomActivity.class));
//            }else {
//                WebSocketLogic.getInstance().connect();
//            }
//            testRegister();


//            Log.i(TAG,"md5 1 = "+md5(1+""));

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        //Your code goes here
//                        testGet();
//                        testRegister();
                        testLogin();
//                        makeGetRequest();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

        } else if(mBtDisConn == v){
            WebSocketLogic.getInstance().disconnect();
        }
    }

    private void writeStream(OutputStream out,byte[] stream) {

        BufferedOutputStream bos = null ;

        Log.i(TAG,"writeStream()");

        try {
            bos = new BufferedOutputStream(out);
            bos.write(stream);
            bos.flush();
        } catch (IOException e) {
            Log.e(TAG, ""+e.toString());
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readStream(InputStream in) {

        Log.i(TAG,"readStream()");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuffer sb = new StringBuffer();


            while ((line = reader.readLine()) != null) {
                sb.append(line+"\n");
            }

            Log.i(TAG,"sb = "+sb.toString());
        } catch (IOException e) {
            Log.e(TAG, ""+e.toString());
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}


