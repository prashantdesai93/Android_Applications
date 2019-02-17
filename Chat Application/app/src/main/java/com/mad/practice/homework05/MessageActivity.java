package com.mad.practice.homework05;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.RemoveMessageCallback{

    SharedPreferences prefs;
    ListView listView;
    TextView tvNameHeadMsg;
    AllMessages allMessages;
    MessageAdapter adapter;
    EditText etMessage;
    Handler h;
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setTitle("Chatroom");
        listView = findViewById(R.id.lvMessage);

        prefs = getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MessageActivity.MODE_PRIVATE);

        tvNameHeadMsg = findViewById(R.id.tvNameHeadMsg);
        tvNameHeadMsg.setText(getIntent().getStringExtra("threadName"));

        findViewById(R.id.ivHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Gson gson = new Gson();

        if(getIntent() != null && getIntent().getExtras() != null ){

            String strObj = getIntent().getStringExtra("allMessages");
            allMessages = gson.fromJson(strObj, AllMessages.class);
            List<Messages> sortedMessages = allMessages.messages;
            Collections.sort(sortedMessages,Messages.MessageTimeComparator);
            Log.d("demo", "MessageThreadActivity: " + allMessages);

            adapter = new MessageAdapter(MessageActivity.this, R.layout.thread_item, sortedMessages);
            adapter.setCallback(this);
            listView.setAdapter(adapter);
            Log.d("demo", "onCreate: after adapter");

        }


        etMessage = findViewById(R.id.etMessage);

        findViewById(R.id.ivSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etMessage.getText().length() > 0) {
                    Log.d("demo", "onClick: inside message send ");
                    if(isConnected()){
                        addMessage(new Messages(etMessage.getText().toString()));
                    } else{
                        Toast.makeText(MessageActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        h = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0){
                    refresh(msg.getData().getString("messageResponse"));
                }else{
                    printToast(msg.getData().getString("error"));
                }
            }
        };


    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    public void refresh(String messageResponse){
        Gson gson = new Gson();
        allMessages = gson.fromJson(messageResponse, AllMessages.class);
        List<Messages> sortedMessages = allMessages.messages;
        Collections.sort(sortedMessages,Messages.MessageTimeComparator);
        adapter = new MessageAdapter(MessageActivity.this, R.layout.thread_item, sortedMessages);
        adapter.setCallback(this);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        etMessage.setText("");
    }

    public void printToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void addMessage(Messages messages){

        Log.d("demo", "addMessage: "+messages.message+" "+getIntent().getExtras().getString("threadId"));
        String token = prefs.getString("token","");
        RequestBody formBody = new FormBody.Builder()
                .add("message", messages.message)
                .add("thread_id", getIntent().getExtras().getString("threadId"))
                .build();


        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/message/add")
                .header("Authorization", "BEARER " + prefs.getString("token",""))
                .post(formBody)
                .build();

        if(token.length() > 0) {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String addMessageResponse = response.body().string();

                    Log.d("demo", "addMessageResponse : onResponse: "+addMessageResponse);
                    Gson gson = new Gson();
                    AllMessageResp allMessageResp = gson.fromJson(addMessageResponse, AllMessageResp.class);

                    if(allMessageResp.status.equalsIgnoreCase("ok")){
                        getNewMessages();
                        Log.d("demo", "onResponse: yayayayayayayayayay");
                    }
                    else{
                        Message message = new Message();
                        message.what = 1;

                        Bundle bundle = new Bundle();
                        bundle.putString("error","Something went wrong");
                        message.setData(bundle);
                        h.sendMessage(message);
                    }
                }
            });
        }

    }

    public void getNewMessages(){
        Log.d("demo", "Messageactivity "+getIntent().getExtras().getString("threadId"));

        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/messages/"+getIntent().getExtras().getString("threadId"))
                .header("Authorization", "BEARER " + prefs.getString("token",""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String messageResponse = response.body().string();
                Log.d("demo", "MainActivity getMessageThreads : onResponse: "+messageResponse);

                Gson gson = new Gson();
                AllMessages allMessages = gson.fromJson(messageResponse, AllMessages.class);

                if(allMessages.status.equalsIgnoreCase("ok")) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("messageResponse", messageResponse);
                    message.what = 0;
                    message.setData(bundle);
                    h.sendMessage(message);
                } else{
                    Message message = new Message();
                    message.what = 1;

                    Bundle bundle = new Bundle();
                    bundle.putString("error",allMessages.message);
                    message.setData(bundle);
                    h.sendMessage(message);
                }


            }
        });
    }

    @Override
    public void removeMessage(Messages m) {
        Log.d("demo", "MessageThreadAdapter : onClick: thread = "+m.toString());
        //Toast.makeText(this, "onclick" + thread.id, Toast.LENGTH_SHORT).show();
        String token = prefs.getString("token","");
        String url = "http://ec2-54-91-96-147.compute-1.amazonaws.com/api/message/delete/"+m.id;
        Log.d("demo", "MessageThreadAdapter : onClick: token "+token);
        Log.d("demo", "MessageThreadAdapter : onClick: url "+url);
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "BEARER " + token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("demo", "MessageThreadAdapter : onFailure: "+e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("demo", "onResponse: delete thread "+response.body().string());
                getNewMessages();
            }
        });
    }
}
