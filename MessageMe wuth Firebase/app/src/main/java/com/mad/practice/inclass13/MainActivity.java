package com.mad.practice.inclass13;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class MainActivity extends AppCompatActivity {
    String TAG = "demo";
    FirebaseAuth mAuth;
    private DatabaseReference mDatabase;String email, password;
    EditText etUserName, etPassword;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        etUserName = findViewById(R.id.etUsename);
        etPassword = findViewById(R.id.etPassword);
        //etUserName.setText("123");
        //etPassword.setText("123");
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
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        performLogin(email, password);
                        //performLogin("kkorey@uncc.edu", "123456");
                    } else{
                        Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        findViewById(R.id.btnSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(getApplicationContext(),SignUpActivity.class);
                finish();
                startActivity(i);
            }
        });


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

    private void performLogin(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Logged in Successfully",
                                    Toast.LENGTH_SHORT).show();
                            getMessageThreads(mAuth.getCurrentUser());
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    public void getMessageThreads(FirebaseUser user){
        Log.d(TAG, "getMessageThreads");
        Intent i = new Intent(MainActivity.this, MessageThreadsActivity.class);
        startActivity(i);
    }

}

