package com.mad.practice.inclass10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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
                        signUp(u,etPwd.getText().toString(),etCpwd.getText().toString());
                    }else{
                        etCpwd.setError("Passwords Do not Match");
                    }
                }else{
                    Toast.makeText(SignUp.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
                Log.d("demo", "SignUp : onResponse: "+response.body().string());
                finish();
            }
        });

    }
}
