package com.example.chenwei.testwebsocket;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 聊天室
 * @author wei.chen
 */
public class ChatRoomActivity extends ActionBarActivity implements View.OnClickListener{

    private final String TAG = "chenwei.ChatRoom";

    /** 房间名 */
    private TextView mTVTitle;
    /** 聊天室成员*/
    private TextView mTVPeoples;
    /** 聊天信息*/
    private TextView mTVShowmsg;
    /** 编辑信息*/
    private EditText mEdMsg;
    /** 发送按钮*/
    private Button mBtSend;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {

            int what = -1;
            what = msg.what;
            if(what == WebSocketLogic.MSG_CONNECT_FAIL){

                String tmp = (String) msg.obj;
                Toast.makeText(ChatRoomActivity.this,"连接服务器失败，请重新尝试！["+tmp+"]",Toast.LENGTH_SHORT).show();

            } else if(what == WebSocketLogic.MSG_DISCONNECT){
                String tmp = (String) msg.obj;
                Toast.makeText(ChatRoomActivity.this,"服务器断开,退出聊天室！["+tmp+"]",Toast.LENGTH_SHORT).show();
                finish();
            } else if(what == WebSocketLogic.MSG_SYSTEM_NOTIFY){
                String tmp = (String) msg.obj;
                Toast.makeText(ChatRoomActivity.this,tmp,Toast.LENGTH_SHORT).show();
                updateView();
            } else if(what == WebSocketLogic.MSG_SHOW_CHAT_CONTENT){
                String tmp = (String) msg.obj;
                updateContent(tmp);
            }
//            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        initView();
    }

    /**
     * 初始化界面
     */
    private void initView(){

        mTVTitle = (TextView) this.findViewById(R.id.chatroom_title);
        mTVPeoples = (TextView) this.findViewById(R.id.chatroom_peoples);
        mTVShowmsg = (TextView) this.findViewById(R.id.chatroom_showmsg);

        mEdMsg = (EditText) this.findViewById(R.id.chatroom_editmsg);

        mBtSend = (Button) this.findViewById(R.id.chatroom_send);
        mBtSend.setOnClickListener(this);
    }

    /**
     * 更新界面
     */
    private void updateView(){

        String roomName = WebSocketLogic.getInstance().getRoomName();
        int count = WebSocketLogic.getInstance().getCount();

        mTVTitle.setText(roomName+"--- （"+count+"）人");
        mTVPeoples.setText("房间成员： "+WebSocketLogic.getInstance().getPeopeleId());
    }

    private StringBuffer sb = new StringBuffer();
    /**
     * 更新聊天内容
     */
    private void updateContent(String str){
        sb.append(str+"\n");
        mTVShowmsg.setText(sb.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        WebSocketLogic.getInstance().addHandler(mHandler);
        Toast.makeText(this,"欢迎进入私人聊天室！",Toast.LENGTH_SHORT).show();

        if(!WebSocketLogic.getInstance().isConnected()){

            finish();
            return;
        }

        updateView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        WebSocketLogic.getInstance().removeHandler(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketLogic.getInstance().disconnect();

        Toast.makeText(this,"已退出聊天室！",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_room, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 发送信息
     */
    private void sendMsg(){
        String str = mEdMsg.getText().toString();
        if(TextUtils.isEmpty(str)){
            Toast.makeText(this,"不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject json=new JSONObject();
        try {
            json.put("type","3");
            json.put("id",WebSocketLogic.getInstance().getPeopeleId());
            json.put("msg",str);
            json.put("time",Tool.getCurTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String tmp = json.toString();
        Log.i(TAG,"json="+tmp);
        WebSocketLogic.getInstance().sendMsg(json.toString());
        mEdMsg.setText("");
    }

    @Override
    public void onClick(View v) {
        if(mBtSend == v){
            sendMsg();
        }
    }
}
