package com.mad.practice.homework05;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {    String TAG = "demo";
    String email, password;
    EditText etUserName, etPassword;
    private final OkHttpClient client = new OkHttpClient();
    public static String SHARED_PREFS_NAME = "SharedPrefs";
    Handler h;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etUserName = findViewById(R.id.etUsename);
        etPassword = findViewById(R.id.etPassword);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error = false;
                try{
                    if(etUserName.getText().length()>1){
                        email = etUserName.getText().toString().trim();
                    }else{
                        error = true;
                        etUserName.setError("Required");
                    }
                    if(etPassword.getText().length()>1){
                        password = etPassword.getText().toString();
                    }else{
                        error = true;
                        etPassword.setError("Required");
                    }
                }catch(Exception e){

                }

                if(!error){
                    if(isConnected()) {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.show();
                        performLogin(email, password);
                    } else{
                        Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getApplicationContext(),SignUp.class);
                finish();
                startActivity(i);
            }
        });

        h = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0){
                    progressDialog.dismiss();
                    etUserName.setText("");
                    etPassword.setText("");
                }else{
                    progressDialog.dismiss();
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

    public void printToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void performLogin(String email, String password){

        Log.d(TAG, "performLogin");

        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/login")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "on fail: ");
                //Toast.makeText(MainActivity.this, "Login Failed Please Try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String loginResponse = response.body().string();

                Log.d("demo", "MainActivity performLogin : onResponse: "+loginResponse);
                Gson gson = new Gson();
                User user = gson.fromJson(loginResponse, User.class);

                if(user.getStatus().equalsIgnoreCase("ok")){

                    Message message = new Message();
                    message.what = 0;
                    h.sendMessage(message);

                    Log.d(TAG, "Token: "+user.getToken());

                    SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREFS_NAME, MainActivity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("token", user.getToken());
                    editor.putString("fName", user.getUser_fname());
                    editor.putString("lName", user.getUser_lname());
                    editor.putInt("userId", user.getUser_id());
                    editor.commit();

                    getMessageThreads(user);
                }else{
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

    public void getMessageThreads(User user){
        Log.d(TAG, "getMessageThreads");

        Request request = new Request.Builder()
                .url("http://ec2-54-91-96-147.compute-1.amazonaws.com/api/thread")
                .header("Authorization", "BEARER " + user.getToken())
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

                Intent i = new Intent(MainActivity.this, MessageThreadsActivity.class);
                i.putExtra("messageThreads", gson.toJson(allMessageThreads));
                startActivity(i);
            }
        });
    }
}


