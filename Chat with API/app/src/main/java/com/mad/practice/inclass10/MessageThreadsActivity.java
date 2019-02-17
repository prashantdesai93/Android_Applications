package com.mad.practice.inclass10;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MessageThreadsActivity extends AppCompatActivity {


    ImageView ivLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_threads);


        final SharedPreferences prefs = getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MessageThreadsActivity.MODE_PRIVATE);

        findViewById(R.id.ivLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefs.edit().clear().commit();
                finish();
            }
        });
    }

}
