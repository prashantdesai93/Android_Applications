package com.mad.practice.inclass13;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MessageThreadsActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    ListView listView;
    MessageThreadAdapter adapter;

    String TAG = "demo";
    List<Message> mailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_threads);
        setTitle("Inbox");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("allMails").child(user.getUid());

        listView = findViewById(R.id.lvMails);
        mailList= new ArrayList<Message>();
        Log.d(TAG, "onCreate: 3");
        initData(mailList);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Message message = dataSnapshot.getValue(Message.class);
                Log.d(TAG, "Thread onChildAddedddd");
                adapter.add(message);
                adapter.sort(new Comparator<Message>() {
                    DateFormat f = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
                    @Override
                    public int compare(Message o1, Message o2) {
                        try {
                            Date d1 = f.parse(o1.date);
                            Date d2 = f.parse(o2.date);
                            return d2.compareTo(d1);
                        } catch (ParseException e) {
                            throw new IllegalArgumentException(e);
                        }
                    }
                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "Thread onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Thread onChildRemoved:" + dataSnapshot.getKey());
                /*Thread thread = dataSnapshot.getValue(Thread.class);
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
                }*/
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("demo", "i'm here");
                Message m = mailList.get(position);
                Log.d(TAG, "Thread onItemClick: "+m.toString());
                if(isConnected()) {
                    Intent i = new Intent(MessageThreadsActivity.this, ReadMessageActivity.class);
                    i.putExtra("mail", m.id);
                    finish();
                    startActivity(i);
                } else{
                    Toast.makeText(MessageThreadsActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        myRef.addChildEventListener(childEventListener);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_message) {
            if(isConnected()) {
                Intent i = new Intent(MessageThreadsActivity.this, ComposeMessageActivity.class);
                finish();
                startActivity(i);
            } else{
                Toast.makeText(MessageThreadsActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if(id == R.id.logout){
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    public void signOut(){
        if(isConnected()) {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(MessageThreadsActivity.this, MainActivity.class);
            finish();
        } else{
            Toast.makeText(MessageThreadsActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
        }
        //startActivity(i);
    }



    public void initData(List<Message> mails){
        adapter = new MessageThreadAdapter(MessageThreadsActivity.this, R.layout.message_item, mails);
        //adapter.setCallback(this);
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
        //etNewTread.setText("");
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


}
