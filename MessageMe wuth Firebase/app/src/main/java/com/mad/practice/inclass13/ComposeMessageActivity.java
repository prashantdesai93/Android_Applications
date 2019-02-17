package com.mad.practice.inclass13;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComposeMessageActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    List<User> userList;
    String TAG ="demo";
    String [] uArr;
    AlertDialog.Builder builder;
    AlertDialog alert;
    User selectedUser;
    TextView tvTo;
    Button btnSend;
    EditText etMsg;
    Message message;
    ValueEventListener postListener;
    String mailId;
    ImageView ivTo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_message);
        setTitle("Compose Message");

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        userList = new ArrayList<>();

        if(isConnected()){
            getAllUsers();
        } else{
            Toast.makeText(ComposeMessageActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
        }


        tvTo =findViewById(R.id.tvTo);
        etMsg = findViewById(R.id.etMsg);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setEnabled(false);
        ivTo = findViewById(R.id.ivTo);

        ivTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uArr= new String [userList.size()] ;
                int i=0;
                Log.d(TAG, "onClick: "+userList.toString());
                for(User u : userList){
                    uArr[i++]=u.fName +" "+u.lName;
                }
                builder= new AlertDialog.Builder(ComposeMessageActivity.this);

                builder.setTitle("Select User")
                        .setItems(uArr,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("demo", "Selected " + uArr[which]);
                                selectedUser = userList.get(which);
                                tvTo.setText(uArr[which]);
                                btnSend.setEnabled(true);
                            }
                        });
                alert = builder.create();
                alert.show();
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message m = new Message();
                m.senderId= user.getUid();
                m.message = etMsg.getText().toString().trim();
                m.isMessageRead=false;
                m.senderName=user.getDisplayName();
                m.date= new Date().toString();

                if(etMsg.getText().length() > 0) {
                    if(isConnected()) {
                        addMail(m);
                        Intent i = new Intent(ComposeMessageActivity.this, MessageThreadsActivity.class);
                        finish();
                        startActivity(i);
                    } else{
                        Toast.makeText(ComposeMessageActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                } else{
                    etMsg.setError("Enter Message");
                }

            }

        });

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                message = dataSnapshot.getValue(Message.class);
                if(message !=null){
                    Log.d(TAG, "Message onDataChange in compose when replied: "+message.toString());
                    tvTo.setText(message.senderName);
                    selectedUser = new User();
                    selectedUser.id = message.senderId;
                    ivTo.setEnabled(false);
                    btnSend.setEnabled(true);
                   // tvReadMessage.setText(message.message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        if(getIntent() != null && getIntent().getExtras() != null ){
            mailId = getIntent().getStringExtra("mail");
            myRef.child("allMails").child(user.getUid()).child(mailId).addListenerForSingleValueEvent(postListener);
        }

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

    public void getAllUsers(){
        //final List<User> uList=new ArrayList<>();
        Log.d(TAG, "getAllUsers: ");
        final ProgressDialog p = new ProgressDialog(ComposeMessageActivity.this);
        p.setCancelable(false);
        Query queryRef = myRef.child("users");//we need to iterate them backwards
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG, "Thread onDataChange:renderData"+snapshot);
                for (DataSnapshot threadSnapshot : snapshot.getChildren()) {
                    userList.add(threadSnapshot.getValue(User.class));
                }
                p.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.toString());
                p.dismiss();
            }
        });

        //Log.d(TAG, "getAllUsers: "+uList.toString());
        //return uList;
    }

    public void addMail(Message message){

        String key = myRef.child("allMails").child(selectedUser.id).push().getKey();
        message.id= key;
        Map<String, Object> threadValues = message.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, threadValues);
        myRef.child("allMails").child(selectedUser.id).updateChildren(childUpdates);
        Toast.makeText(ComposeMessageActivity.this, "Sent", Toast.LENGTH_SHORT).show();
        //etNewTread.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ComposeMessageActivity.this,MessageThreadsActivity.class);
        finish();
        startActivity(i);
    }
}
