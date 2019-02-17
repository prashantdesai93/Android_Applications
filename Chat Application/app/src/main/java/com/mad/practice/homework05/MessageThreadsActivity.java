package com.mad.practice.homework05;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageThreadsActivity extends AppCompatActivity implements MessageThreadAdapter.RemoveThreadCallback{

    ImageView ivLogout;
    TextView tvNameHead;
    EditText etNewTread;
    AllMessageThreads allMessageThreads;
    MessageThreadAdapter adapter;
    SharedPreferences prefs;
    ListView listView;
    Handler h;

    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_threads);
        setTitle("Message Threads");

        listView = findViewById(R.id.lvThreads);

        prefs = getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MessageThreadsActivity.MODE_PRIVATE);
        String firstName = prefs.getString("fName","");
        String lastName = prefs.getString("lName","");

        tvNameHead = findViewById(R.id.tvNameHead);
        tvNameHead.setText( firstName + " " + lastName);

        findViewById(R.id.ivLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().clear().commit();
                finish();
                Intent i = new Intent(MessageThreadsActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        Gson gson = new Gson();

        if(getIntent() != null && getIntent().getExtras() != null ){

            String strObj = getIntent().getStringExtra("messageThreads");
            allMessageThreads = gson.fromJson(strObj, AllMessageThreads.class);
            Log.d("demo", "MessageThreadActivity: " + allMessageThreads);

            adapter = new MessageThreadAdapter(MessageThreadsActivity.this, R.layout.thread_item, allMessageThreads.threads);
            adapter.setCallback(this);
            listView.setAdapter(adapter);
            Log.d("demo", "onCreate: after adapter");

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /*Log.d("demo", "i'm here"+news);
                    Article art = news.get(position);
                    Intent i = new Intent(NewsList.this,NewsDetails.class);
                    i.putExtra(NewsList.EACH_NEWS_KEY,art);
                    startActivity(i);*/
                    //Toast.makeText(getApplicationContext(), "i'm here at "+allMessageThreads.threads.get(position), Toast.LENGTH_SHORT).show();
                    if(isConnected()){
                        getAllMessages(allMessageThreads.threads.get(position).id, allMessageThreads.threads.get(position).title);
                    } else{
                        Toast.makeText(MessageThreadsActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


        etNewTread = findViewById(R.id.etNewTread);

        findViewById(R.id.ivAddThread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etNewTread.getText().length() > 0) {
                    Log.d("demo", "onClick: inside ");

                    if(isConnected()) {
                        addThread(new Thread(etNewTread.getText().toString()));
                    } else{
                        Toast.makeText(MessageThreadsActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        h = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0){
                    refresh(msg.getData().getString("messageThreadResponse"));
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

    public void refresh(String messageThreadResponse){
        Gson gson = new Gson();
        allMessageThreads = gson.fromJson(messageThreadResponse, AllMessageThreads.class);
        adapter = new MessageThreadAdapter(MessageThreadsActivity.this, R.layout.thread_item, allMessageThreads.threads);
        adapter.setCallback(this);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        etNewTread.setText("");
    }

    public void printToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void addThread(Thread thread){

        String token = prefs.getString("token","");
        RequestBody formBody = new FormBody.Builder()
                .add("title", thread.title)
                .build();


        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread/add")
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
                    String addThreadResponse = response.body().string();

                    //Log.d("demo", "addThreadResponse : onResponse: "+addThreadResponse);
                    Gson gson = new Gson();
                   User user = gson.fromJson(addThreadResponse, User.class);

                    if(user.getStatus().equalsIgnoreCase("ok")){
                        getMessageThreads();
                    }
                    else{
                        Message message = new Message();
                        message.what = 1;

                        Bundle bundle = new Bundle();
                        bundle.putString("error",user.getMessage());
                        message.setData(bundle);
                        h.sendMessage(message);
                    }
                }
            });
        }

    }

    public void getMessageThreads(){
        Log.d("demo", "Messageactivity");

        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread")
                .header("Authorization", "BEARER " + prefs.getString("token",""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String messageThreadResponse = response.body().string();
                Log.d("demo", "MainActivity getMessageThreads : onResponse: "+messageThreadResponse);

                Gson gson = new Gson();
                AllMessageThreads allMessageThreads = gson.fromJson(messageThreadResponse, AllMessageThreads.class);

                if(allMessageThreads.status.equalsIgnoreCase("ok")) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("messageThreadResponse", messageThreadResponse);
                    message.what = 0;
                    message.setData(bundle);
                    h.sendMessage(message);
                } else{
                    Message message = new Message();
                    message.what = 1;

                    Bundle bundle = new Bundle();
                    bundle.putString("error",allMessageThreads.message);
                    message.setData(bundle);
                    h.sendMessage(message);
                }


            }
        });
    }

    public void getAllMessages(final int id, final String threadName){
        Log.d("demo", "getAllMessages");

        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/messages/"+id)
                .header("Authorization", "BEARER " + prefs.getString("token",""))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                String messageResponse = response.body().string();
                Log.d("demo", "MainActivity getAllMessages : onResponse: "+messageResponse);

                Gson gson = new Gson();
                AllMessages allMessages = gson.fromJson(messageResponse, AllMessages.class);

                Log.d("demo", "MainActivity allMessages gson : onResponse: "+allMessages);


                Intent iMessage = new Intent(MessageThreadsActivity.this, MessageActivity.class);
                iMessage.putExtra("threadName", threadName);
                iMessage.putExtra("threadId", String.valueOf(id));
                iMessage.putExtra("allMessages", gson.toJson(allMessages));
                startActivity(iMessage);

            }
        });

    }

    @Override
    public void removeThread(Thread thread) {
        Log.d("demo", "MessageThreadAdapter : onClick: thread = "+thread.toString());
        //Toast.makeText(this, "onclick" + thread.id, Toast.LENGTH_SHORT).show();
        String token = prefs.getString("token","");
        String url = "http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread/delete/"+thread.id;
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
                getMessageThreads();
            }
        });
    }
}
