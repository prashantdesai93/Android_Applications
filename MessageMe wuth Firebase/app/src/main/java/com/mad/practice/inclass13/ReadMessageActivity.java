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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ReadMessageActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;
    String mailId=null;
    TextView tvFrom, tvReadMessage;
    Message message;
    ValueEventListener postListener;
    String TAG = "demo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_message);
        setTitle("Read Message");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("allMails").child(user.getUid());

        tvFrom = findViewById(R.id.tvFrom);
        tvReadMessage = findViewById(R.id.tvReadMessage);


        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                message = dataSnapshot.getValue(Message.class);
                if(message !=null){
                    Log.d(TAG, "Message onDataChange for thread: "+message.toString());
                    tvFrom.setText("From: "+message.senderName);
                    tvReadMessage.setText(message.message);
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
            myRef.child(mailId).child("isMessageRead").setValue(true);
            myRef.child(mailId).addListenerForSingleValueEvent(postListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.read_message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.trash) {

            if(isConnected()) {
                removeMessage(message);
                Intent i = new Intent(ReadMessageActivity.this, MessageThreadsActivity.class);
                finish();
                startActivity(i);
            } else{
                Toast.makeText(ReadMessageActivity.this, "Please connect to Internet", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        if (id == R.id.reply) {
            Intent i = new Intent(ReadMessageActivity.this,ComposeMessageActivity.class);
            i.putExtra( "mail", message.id);
            finish();
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void removeMessage(Message m) {
        Log.d(TAG, "removeMessage: "+m.id);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(m.id, null);
        myRef.updateChildren(childUpdates);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ReadMessageActivity.this,MessageThreadsActivity.class);
        finish();
        startActivity(i);
    }
}