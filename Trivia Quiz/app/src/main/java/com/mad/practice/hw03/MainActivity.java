package com.mad.practice.hw03;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GetQuestionsAsync.IData {

    static String QUESTION_KEY ="Questions";
    ProgressBar progressBar;
    ArrayList<Question> questionsList;
    ImageView ivWelcome;
    TextView tvReady;
    TextView tvLoading;
    Button btnStart;
    Button btnExit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar2);
        ivWelcome = findViewById(R.id.ivWelcome);
        tvLoading = findViewById(R.id.tvLoading);
        tvReady = findViewById(R.id.tvReady);
        btnStart = findViewById(R.id.btnStart);
        btnExit = findViewById(R.id.btnExit);
        questionsList= new ArrayList<Question>();

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, TriviaActivity.class);
                i.putExtra(MainActivity.QUESTION_KEY,questionsList);
                finish();
                startActivity(i);
            }
        });

        if(isConnected()){
            new GetQuestionsAsync(MainActivity.this, progressBar, ivWelcome).execute("http://dev.theappsdr.com/apis/trivia_json/trivia_text.php");
        }else{
            Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
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

    @Override
    public void handleQuestions(ArrayList<Question> data) {
        progressBar.setVisibility(View.GONE);
        if(data!=null && data.size()>0) {
            Log.d("handleQuestions", data.toString() + "" + data.size());
            questionsList = data;
            tvLoading.setVisibility(View.INVISIBLE);
            tvReady.setVisibility(View.VISIBLE);
            btnStart.setEnabled(true);
        }

    }
}
