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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    Handler h;
    ProgressDialog progressDialog;
    private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        final EditText etFname = (EditText) findViewById(R.id.etFname);
        final EditText etLname = (EditText) findViewById(R.id.etLname);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPwd = (EditText) findViewById(R.id.etPwd);
        final EditText etCpwd = (EditText) findViewById(R.id.etCpwd);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        Button btnSignup = (Button) findViewById(R.id.btnSignup1);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(SignUp.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnSignup.findViewById(R.id.btnSignup1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User u = new User();
                String pass = null;
                String cPass = null;
                boolean error=false;
                try{
                    if(etEmail.getText().length()>1){
                        u.setUser_email(etEmail.getText().toString());
                    }else{
                        error = true;
                        etEmail.setError("Required");
                    }
                    if(etFname.getText().length()>1){
                        u.setUser_fname(etFname.getText().toString());
                    }else{
                        error = true;
                        etFname.setError("Required");
                    }
                    if(etLname.getText().length()>1){
                        u.setUser_lname(etLname.getText().toString());
                    }else{
                        error = true;
                        etLname.setError("Required");
                    }
                    if(etPwd.getText().length()>1){
                         pass =etPwd.getText().toString();
                    }else{
                        error = true;
                        etPwd.setError("Required");
                    }
                    if(etCpwd.getText().length()>1){
                        cPass =etCpwd.getText().toString();
                    }else{
                        error = true;
                        etCpwd.setError("Required");
                    }
                }catch(Exception e){

                }

                if(!error){
                    if(pass.equalsIgnoreCase(cPass)){
                        if(isConnected()){
                            progressDialog = new ProgressDialog(SignUp.this);
                            progressDialog.show();
                            signUp(u,etPwd.getText().toString(),etCpwd.getText().toString());
                        } else{
                            Toast.makeText(SignUp.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        etCpwd.setError("Passwords Do not Match");
                    }
                }else{
                    Toast.makeText(SignUp.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        h = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0){
                    progressDialog.dismiss();
                    printToast(msg.getData().getString("success"));
                }else{
                    progressDialog.dismiss();
                    printToast(msg.getData().getString("error"));
                }
            }
        };
    }

    public void printToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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

    public void signUp(User u, String pass, String cPass){
        OkHttpClient client = new OkHttpClient();
        String url = "http://ec2-54-91-96-147.compute-1.amazonaws.com/api/signup";

        RequestBody formBody = new FormBody.Builder()
                .add("fname", u.getUser_fname())
                .add("lname",u.getUser_lname())
                .add("email",u.getUser_email())
                .add("password",pass)
                .build();



        Request request =new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String signUpResponse = response.body().string();
                Log.d("demo", "SignUp : onResponse: "+signUpResponse);
                Gson gson = new Gson();
                User user = gson.fromJson(signUpResponse, User.class);

                if(user.getStatus().equalsIgnoreCase("ok")){
                    Message message = new Message();
                    message.what = 0;

                    Bundle bundle = new Bundle();
                    bundle.putString("success","User has been created successfully");
                    message.setData(bundle);
                    h.sendMessage(message);

                    SharedPreferences sharedpreferences = getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MainActivity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("token", user.getToken());
                    editor.putString("fName", user.getUser_fname());
                    editor.putString("lName", user.getUser_lname());
                    editor.putInt("userId", user.getUser_id());
                    editor.commit();

                    getMessageThreads(user);
                } else{
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
        Log.d("demo", "getMessageThreads");

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

                Intent i = new Intent(SignUp.this, MessageThreadsActivity.class);
                i.putExtra("messageThreads", gson.toJson(allMessageThreads));
                finish();
                startActivity(i);
            }
        });
    }
}
