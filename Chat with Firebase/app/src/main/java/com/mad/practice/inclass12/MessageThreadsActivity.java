package com.mad.practice.inclass12;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.FirebaseError;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageThreadsActivity extends AppCompatActivity implements MessageThreadAdapter.RemoveThreadCallback {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    ImageView ivLogOut;
    TextView tvNameHead;
    EditText etNewTread;
    ListView listView;
    MessageThreadAdapter adapter;

    String TAG = "demo";
    List<Thread> threadList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_threads);
        setTitle("Message Threads");

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("allThreads");

        tvNameHead = findViewById(R.id.tvNameHead);
        tvNameHead.setText(user.getDisplayName());
        etNewTread = findViewById(R.id.etNewTread);

        ivLogOut = findViewById(R.id.ivLogout);
        ivLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        listView = findViewById(R.id.lvThreads);
        threadList= new ArrayList<Thread>();
        Log.d(TAG, "onCreate: 3");
        initData(threadList);
        findViewById(R.id.ivAddThread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etNewTread.getText().length() > 0) {
                    Log.d("demo", "Thread onClick: inside ");

                    if(isConnected()) {
                        addThread(new Thread(etNewTread.getText().toString(),null,null));
                    } else{
                        Toast.makeText(MessageThreadsActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Thread thread = dataSnapshot.getValue(Thread.class);
                Log.d(TAG, "Thread onChildAddedddd");
                adapter.add(thread);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Thread onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Thread onChildRemoved:" + dataSnapshot.getKey());
                Thread thread = dataSnapshot.getValue(Thread.class);
                Thread toRemove=null;
                for(Thread t : threadList){
                    if(t.id.equals(thread.id)){
                        toRemove=t;
                        break;
                    }
                }
                if(toRemove!=null){
                    adapter.remove(toRemove);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Thread onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Thread onCancelled", databaseError.toException());
                Toast.makeText(MessageThreadsActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        };

        myRef.addChildEventListener(childEventListener);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("demo", "i'm here");
                    Thread t = threadList.get(position);
                    Log.d(TAG, "Thread onItemClick: "+t.toString());
                    Intent i = new Intent(MessageThreadsActivity.this,MessageActivity.class);
                    i.putExtra( "thread",t.id);
                    startActivity(i);
            }
        });


    }

    /*public void renderData() {
        Query queryRef = myRef.child("allThreads");//we need to iterate them backwards
        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d(TAG, "Thread onDataChange:renderData");
                for (DataSnapshot threadSnapshot : snapshot.getChildren()) {
                    threadList.add(threadSnapshot.getValue(Thread.class));
                }
                initData(threadList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: "+databaseError.toString());
            }
        });
    }*/


    public void addThread(Thread thread){
        String key = myRef.push().getKey();
        thread.id = key;
        thread.userId=user.getUid();
        Map<String, Object> threadValues = thread.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, threadValues);
        myRef.updateChildren(childUpdates);
        etNewTread.setText("");
    }

    public void initData(List<Thread> threads){
        adapter = new MessageThreadAdapter(MessageThreadsActivity.this, R.layout.thread_item, threads);
        adapter.setCallback(this);
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        etNewTread.setText("");
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

    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(MessageThreadsActivity.this, MainActivity.class);
        finish();
        //startActivity(i);
    }


    @Override
    public void removeThread(Thread t) {
        Log.d(TAG, "renderData removeThread: "+t.id);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(t.id, null);
        myRef.updateChildren(childUpdates);
    }
}
