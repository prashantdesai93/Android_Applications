package com.mad.practice.hw03;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class StatActivity extends AppCompatActivity {

    int correctVal;
    int questionSize;
    Button btnQuit;
    Button btnTry;
    int percentage;
    ProgressBar pbPer;
    TextView tvPercent;
    TextView tvTry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
        setTitle("Stat Activity");

        btnQuit = findViewById(R.id.btnQuit);
        btnTry = findViewById(R.id.btnTry);
        pbPer = findViewById(R.id.pbPer);
        tvPercent = findViewById(R.id.tvPercent);
        tvTry = findViewById(R.id.tvTry);

        if (getIntent() != null) {
            correctVal = getIntent().getExtras().getInt(TriviaActivity.CORRECT_KEY);
            questionSize = getIntent().getExtras().getInt(TriviaActivity.QSIZE);
            Log.d("correct ans ", String.valueOf(correctVal));
            percentage = (correctVal * 100)/questionSize;
            Log.d("percentage ", String.valueOf(percentage));
            tvPercent.setText(percentage+" %");
            pbPer.setProgress(percentage);
            if(percentage == 100){
                tvTry.setText("Bingo!!!");
            }
        }

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(StatActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        btnTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(StatActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
    
}
