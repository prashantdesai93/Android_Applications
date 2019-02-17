package com.mad.practice.inclass06;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GetArticleAsync.IData {
    public static String [] category ={"business", "entertainment", "general", "health", "science", "sports", "technology"};
    EditText etSearch;
    TextView tvTitle;
    TextView tvPublish;
    TextView tvDesc;
    TextView tvCount;
    ImageView ivMainImage;
    ProgressDialog progressDialog;
    ArrayList<Article> news;
    ImageButton ibNextBtn;
    ImageButton ibPrevBtn;
    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etSearch = findViewById(R.id.etSearch);
        tvTitle = findViewById(R.id.tvTitle);
        tvPublish = findViewById(R.id.tvPublish);
        tvDesc = findViewById(R.id.tvDesc);
        tvCount = findViewById(R.id.tvCount);
        progressDialog = new ProgressDialog(this);
        news= new ArrayList<Article>();


        ivMainImage = findViewById(R.id.ivMainImage);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Keyword")
                .setItems(category, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etSearch.setText(category[which]);
                        //count=0;
                        if(isConnected()){
                            count=0;
                            new GetArticleAsync(MainActivity.this,progressDialog).execute("https://newsapi.org/v2/top-headlines?country=us&category="+category[which]+"&apiKey=593e863e646143639bc244706c81875f");
                        }else{
                            Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setCancelable(false);
        final AlertDialog alertDialog = builder.create();
        findViewById(R.id.btnGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });

        ibPrevBtn = findViewById(R.id.ibPrev);
        ibPrevBtn.setClickable(false);
        ibPrevBtn.setEnabled(false);
        ibPrevBtn.setVisibility(View.GONE);

        ibNextBtn = findViewById(R.id.ibNext);
        ibNextBtn.setClickable(false);
        ibNextBtn.setEnabled(false);
        ibNextBtn.setVisibility(View.GONE);

        findViewById(R.id.ibNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextImage();
            }
        });
        findViewById(R.id.ibPrev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevImage();
            }
        });
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
    public void handleArticles(ArrayList<Article> articles) {
        //news = articles;

        if(articles.size() > 1){
            ibPrevBtn.setClickable(true);
            ibPrevBtn.setEnabled(true);
            ibNextBtn.setClickable(true);
            ibNextBtn.setEnabled(true);
            ibPrevBtn.setVisibility(View.VISIBLE);
            ibNextBtn.setVisibility(View.VISIBLE);

        }
        if(articles!=null && articles.size()>0){
            Log.i("handleUrls", articles.toString());
            news=articles;
            if(isConnected()){
                ShowNews(0);
            }else{
                Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        }else{
            ivMainImage.setImageResource(android.R.color.transparent);
            Toast.makeText(MainActivity.this, "No News Found", Toast.LENGTH_SHORT).show();
            ibPrevBtn.setClickable(false);
            ibPrevBtn.setEnabled(false);
            ibNextBtn.setClickable(false);
            ibNextBtn.setEnabled(false);
            ibPrevBtn.setVisibility(View.GONE);
            ibNextBtn.setVisibility(View.GONE);
        }
    }

    public void nextImage(){
        count++;
        if(count == news.size()){
            count = 0;
        }
        if(count >-1 && news.size()>0){
            ShowNews(count);
        }
    }

    public void prevImage(){
        count--;
        if(count == -1 && news.size()>0){
            count = news.size()-1;
        }
        if(news.size()>0 && count >-1 && count<news.size()){
            ShowNews(count);
        }
    }

    public void ShowNews(int index){
        Article eachArt= news.get(index);
        tvTitle.setText(eachArt.getTitle());
        tvPublish.setText(eachArt.getPublishedAt());
        tvDesc.setText(eachArt.getDescription().equals("null")?"" :eachArt.getDescription());
        tvCount.setText(index+1+" out of "+news.size());
        if(eachArt.getUrlToImg()!=null && !eachArt.getUrlToImg().equalsIgnoreCase("null")){
            Picasso.with(MainActivity.this).load(eachArt.getUrlToImg()).into(ivMainImage);
        }else{
            ivMainImage.setImageResource(R.drawable.noimage);
            //Toast.makeText(MainActivity.this, "No Image Found", Toast.LENGTH_SHORT).show();
        }
    }
}

