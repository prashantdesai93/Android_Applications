package com.mad.practice.hw03;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Visibility;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TriviaActivity extends AppCompatActivity {

    static String CORRECT_KEY ="CorrectVal";
    static String QSIZE="QuestionSize";
    ArrayList<Question> qList = null;
    ProgressBar progressBar;
    ImageView qImage=null;
    TextView qQuestion=null;
    RadioGroup qOptions =null;
    TextView tvQuestNumValue;
    TextView tvSeconds;
    Button next;
    Button btnQuit;
    int count=0;
    int correct=0;
    Intent i;
    CountDownTimer t;
    GetImageAsync img= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);
        setTitle("Trivia Activity");
        progressBar = findViewById(R.id.progressBar);
        final RelativeLayout rlTrivia = (RelativeLayout) findViewById(R.id.rlTrivia);
        tvQuestNumValue = findViewById(R.id.tvQuestNumValue);
        qImage = findViewById(R.id.qImage);
        next = findViewById(R.id.btnNext);
        tvSeconds = findViewById(R.id.tvSeconds);
        btnQuit = findViewById(R.id.btnQuit);
        count=0;
        correct =0;

        i = new Intent(TriviaActivity.this, StatActivity.class);

        if(getIntent() != null) {
            qList = (ArrayList<Question>) getIntent().getExtras().getSerializable(MainActivity.QUESTION_KEY);
            Log.d("Questions in trivia: ",qList.toString());

           t= new CountDownTimer(120000, 1000) {

                public void onTick(long millisUntilFinished) {
                    tvSeconds.setText(millisUntilFinished / 1000 + " Seconds");
                }

                public void onFinish() {
                    tvSeconds.setText("0 Seconds");
                    //Log.d("onFinish", String.valueOf(correct));
                    i.putExtra(TriviaActivity.CORRECT_KEY,correct);
                    i.putExtra(TriviaActivity.QSIZE, qList.size());
                    finish();
                    startActivity(i);
                }
            }.start();
        }

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(TriviaActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(count!=0){
                    int selectedRadioButton =qOptions.getCheckedRadioButtonId();
                    RadioButton selectedAns = findViewById(selectedRadioButton);
                    if(selectedAns!=null){
                        if(selectedAns.getText().toString().equalsIgnoreCase(qList.get(count-1).getOptions().get(qList.get(count-1).getAns()))){
                            correct++;
                            Log.d("Correct", qList.get(count-1).getOptions().get(qList.get(count-1).getAns())+""+correct);
                            //count++;
                        }
                    }
                }

                showQuestion(count++,rlTrivia);

            }
        });
        showQuestion(count++,rlTrivia);
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
    protected void showQuestion(int index,RelativeLayout rl){
        if(index != qList.size()) {
            Log.d("showQuestion", "index: " + index);
            Question q = qList.get(index);
            Log.d("demo", String.valueOf(q.getQuestionNumber()) + " " + qList.size());
            tvQuestNumValue.setText(index+1+" ");
            /*if (qImage == null) {
                qImage = addImage();
                rl.addView(qImage);
            }*/
            if (q.getImgUrl() != null) {
                if(img != null){
                    img.cancel(true);
                }
                if (isConnected()) {
                    img = new GetImageAsync(qImage, progressBar);
                    img.execute(q.getImgUrl());
                } else {
                    Toast.makeText(TriviaActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                }
            } else {
                if(img != null){
                    img.cancel(true);
                }
                progressBar.setVisibility(View.GONE);
                qImage.setImageResource(R.drawable.no_image);
            }

            if (qQuestion == null) {
                qQuestion = addTextView();
                rl.addView(qQuestion);
            }
            if (q.getQuestion() != null) {
                qQuestion.setText(q.getQuestion());
            }

            if (qOptions == null) {
                ScrollView s = addScrollView();
                qOptions = addRadioGroup();
                s.addView(qOptions);
                rl.addView(s);
            }
            if (q.getOptions() != null && q.getOptions().size() > 1) {
                addRadioButton(qOptions, q.getOptions());
            }
        }
        else{
            //Log.d("done ", String.valueOf(correct));
            if(t !=null){
                t.cancel();
            }
            i.putExtra(TriviaActivity.CORRECT_KEY,correct);
            i.putExtra(TriviaActivity.QSIZE, qList.size());
            finish();
            startActivity(i);
        }
    }


    protected ScrollView addScrollView() {


        ScrollView sv = new ScrollView(this); //create the RadioGroup
        //rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW,R.id.qQuestion);
        params.addRule(RelativeLayout.ABOVE ,R.id.btnNext);
        //params.setMargins(60,10,01,0);
        //params.setMarginStart(60);
        sv.setLayoutParams(params);
        sv.setVerticalScrollBarEnabled(false);
        sv.setHorizontalScrollBarEnabled(false);
        return sv;
    }

    protected RadioGroup addRadioGroup() {


        RadioGroup rg = new RadioGroup(this); //create the RadioGroup
        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW,R.id.qQuestion);
        params.addRule(RelativeLayout.ABOVE ,R.id.btnNext);
        params.setMargins(60,10,01,0);
        params.setMarginStart(60);
        rg.setLayoutParams(params);

        return rg;
    }

    protected void addRadioButton(RadioGroup rg, List<String> opts){
        rg.removeAllViews();
        final RadioButton[] rb = new RadioButton[opts.size()];
        for(int i=0; i<opts.size(); i++){
            rb[i]  = new RadioButton(this);
            rb[i].setText(opts.get(i));
            //rb[i].setId(i);
            rg.addView(rb[i]);
        }

    }
    protected TextView addTextView(){
        TextView tv = new TextView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW,R.id.qImage);
        params.setMargins(60,10,01,0);
        params.setMarginStart(60);
        tv.setTextSize(20);
        tv.setLayoutParams(params);
        tv.setId(R.id.qQuestion);
        return tv;
    }

    protected ImageView addImage(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        ImageView img = new ImageView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height/3);
        params.setMargins(20,160,0,0);
        //params.setMarginStart(10);
        img.setLayoutParams(params);
        img.setId(R.id.qImage);
        return img;
    }
}
