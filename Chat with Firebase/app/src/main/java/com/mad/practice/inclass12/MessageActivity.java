package com.mad.practice.inclass12;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

public class MessageActivity extends AppCompatActivity implements MessageAdapter.RemoveMessageCallback {

    FirebaseDatabase database;
    DatabaseReference myRef;//, myRefMsg;
    FirebaseUser user;
    Thread thread=null;
    String TAG ="demo";
    EditText etMessage;
    TextView tvNameHeadMsg;
    ListView listView;
    List<Messages> messageList;
    MessageAdapter adapter;
    String threadId=null;
    ChildEventListener childEventListener;
    ValueEventListener postListener;
    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setTitle("Chat Room");

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("allThreads");
        //myRefMsg = myRef.child("allMessages");

        tvNameHeadMsg = findViewById(R.id.tvNameHeadMsg);
        findViewById(R.id.ivHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView = findViewById(R.id.lvMessage);


        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                thread = dataSnapshot.getValue(Thread.class);
                if(thread !=null){
                    Log.d(TAG, "Message onDataChange for thread: "+thread.toString());
                    tvNameHeadMsg.setText(thread.title);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };

        if(getIntent() != null && getIntent().getExtras() != null ){
            threadId = getIntent().getStringExtra("thread");
            myRef.child(threadId).addListenerForSingleValueEvent(postListener);
        }

        etMessage = findViewById(R.id.etMessage);

        messageList= new ArrayList<Messages>();
        initData(messageList);


        findViewById(R.id.ivSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etMessage.getText().length() > 0) {
                    Log.d("demo", "onClick: inside message send ");
                    if(isConnected()){
                        Date date = new Date();
                        Messages m = new Messages(etMessage.getText().toString(), user.getDisplayName(), null, null, date.toString());
                        addMessage(m);
                    } else{
                        Toast.makeText(MessageActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                Log.d(TAG, "Message onChildAdded");
                adapter.add(messages);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Message onChildChanged:" + dataSnapshot.getKey());
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Message onChildRemoved:" + dataSnapshot.getKey());
                Messages messages = dataSnapshot.getValue(Messages.class);
                Log.d(TAG, "onChildRemoved: "+messages.toString());
                //messageList.iterator() remove(messageList.indexOf(messages));
                Messages toRemove=null;
                for(Messages m : messageList){
                    if(m.id.equals(messages.id)){
                        toRemove=m;
                        break;
                    }
                }
                if(toRemove!=null){
                    messageList.remove(toRemove);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Message onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Message onCancelled", databaseError.toException());
                Toast.makeText(MessageActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        };
        myRef.child(threadId).child("allMessages").addChildEventListener(childEventListener);
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

    public void addMessage(Messages messages){
        String key = myRef.child(threadId).child("allMessages").push().getKey();
        messages.id = key;
        messages.userId=user.getUid();
        Map<String, Object> threadValues = messages.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, threadValues);
        myRef.child(threadId).child("allMessages").updateChildren(childUpdates);
        etMessage.setText("");
    }

    public void initData(List<Messages> messages){
        adapter = new MessageAdapter(MessageActivity.this, R.layout.message_item, messages);
        adapter.setCallback(this);
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        etMessage.setText("");
    }

    @Override
    public void removeMessage(Messages m) {
        Log.d(TAG, "removeMessage: "+m.id);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(m.id, null);
        myRef.child(threadId).child("allMessages").updateChildren(childUpdates);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ---------------------------");
        //removeEventListener();
        myRef.child(threadId).child("allMessages").removeEventListener(childEventListener);
        myRef.child(threadId).removeEventListener(postListener);
    }
}
