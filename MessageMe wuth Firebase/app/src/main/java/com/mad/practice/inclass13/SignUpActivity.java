package com.mad.practice.inclass13;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private DatabaseReference mDatabase;
    EditText etFname;
    EditText etLname;
    // private final OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");
        etFname = (EditText) findViewById(R.id.etFname);
        etLname = (EditText) findViewById(R.id.etLname);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPwd = (EditText) findViewById(R.id.etPwd);
        final EditText etCpwd = (EditText) findViewById(R.id.etCpwd);
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        Button btnSignup = (Button) findViewById(R.id.btnSignup1);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnSignup.findViewById(R.id.btnSignup1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //User u = new User();
                String pass = null;
                String cPass = null;
                boolean error=false;
                try{
                    if(etEmail.getText().length()>1){
                        //u.setUser_email(etEmail.getText().toString());
                    }else{
                        error = true;
                        etEmail.setError("Required");
                    }
                    if(etFname.getText().length()>1){
                        //u.setUser_fname(etFname.getText().toString());
                    }else{
                        error = true;
                        etFname.setError("Required");
                    }
                    if(etLname.getText().length()>1){
                        //u.setUser_lname(etLname.getText().toString());
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
                    e.printStackTrace();
                }

                if(!error){
                    if(pass.equalsIgnoreCase(cPass)){
                        if(isConnected()){
                            progressDialog = new ProgressDialog(SignUpActivity.this);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            //signUp(u,etPwd.getText().toString(),etCpwd.getText().toString());
                            createAccount(etEmail.getText().toString(),etPwd.getText().toString());
                        } else{
                            Toast.makeText(SignUpActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        etCpwd.setError("Passwords Do not Match");
                    }
                }else{
                    Toast.makeText(SignUpActivity.this, "Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void createAccount(String email, String password) {
        Log.d("demo", "createAccount:" + email);

        // showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("demo", "createUserWithEmail:success "+mAuth.getCurrentUser());
                            FirebaseUser user = mAuth.getCurrentUser();
                            progressDialog.dismiss();
                            updateUserProfile();
                            getMessageThreads();


                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("demo", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            //updateUI(null);
                        }

                    }
                });
        // [END create_user_with_email]
    }

    public  void updateUserProfile(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(etFname.getText().toString()+" "+etLname.getText().toString())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("demo", "User profile updated.");
                            storeUser(user);
                        }
                    }
                });
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


    public void getMessageThreads(){
        Intent i = new Intent(SignUpActivity.this, MessageThreadsActivity.class);
        finish();
        startActivity(i);
    }

    public void storeUser(FirebaseUser user){
        User u = new User();
        u.email= user.getEmail();
        u.fName= etFname.getText().toString().trim();
        u.lName=etLname.getText().toString().trim();
        u.id=user.getUid();
        String key = user.getUid();
        Map<String, Object> userValues = u.toMap();
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put(key, userValues);
        myRef.updateChildren(userUpdates);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SignUpActivity.this,MainActivity.class);
        finish();
        startActivity(i);
    }
}
