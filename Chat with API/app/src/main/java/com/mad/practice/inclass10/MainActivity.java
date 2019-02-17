package com.mad.practice.inclass10;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {    String TAG = "demo";
    String email, password;
    EditText etUserName, etPassword;
    private final OkHttpClient client = new OkHttpClient();
    public static String SHARED_PREFS_NAME = "SharedPrefs";

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
                    performLogin(email, password);
                }
            }
        });

        findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getApplicationContext(),SignUp.class);
                //finish();
                startActivity(i);
            }
        });
    }

    public void performLogin(String email, String password){

        Log.d(TAG, "performLo");

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
                Toast.makeText(MainActivity.this, "Login Failed Please Try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String loginResponse = response.body().string();

                Log.d("demo", "MainActivity : onResponse: "+loginResponse);
                Gson gson = new Gson();
                User user = gson.fromJson(loginResponse, User.class);

                if(user.getStatus().equalsIgnoreCase("ok")){
                    Log.d(TAG, "Token: "+user.getToken());

                    SharedPreferences sharedpreferences = getSharedPreferences(SHARED_PREFS_NAME, MainActivity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString("token", user.getToken());
                    editor.putInt("userId", user.getUser_id());
                    editor.commit();

                    Intent i = new Intent(MainActivity.this, MessageThreadsActivity.class);
                    startActivity(i);
                }else{
                    //Toast.makeText(MainActivity.this, ""+user.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}


