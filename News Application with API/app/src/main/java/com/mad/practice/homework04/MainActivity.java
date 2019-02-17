package com.mad.practice.homework04;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
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

    public static String [] category ={"Top Stories", "World", "U.S.", "Business", "Politics", "Technology", "Health", "Entertainment", "Travel", "Living", "Most Recent"};
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
                            switch (category[which])
                            {
                                case "Top Stories":
                                    new GetArticleAsync(MainActivity.this,progressDialog).execute("http://rss.cnn.com/rss/cnn_topstories.rss");
                                    break;
                                case "World":
                                    new GetArticleAsync(MainActivity.this,progressDialog).execute("http://rss.cnn.com/rss/cnn_world.rss");
                                    break;
                                case "U.S.":
                                    new GetArticleAsync(MainActivity.this,progressDialog).execute("http://rss.cnn.com/rss/cnn_us.rss");
                                    break;
                                case "Business":
                                    new GetArticleAsync(MainActivity.this,progressDialog).execute("http://rss.cnn.com/rss/money_latest.rss");
                                    break;
                                case "Politics":
                                    new GetArticleAsync(MainActivity.this,progressDialog).execute("http://rss.cnn.com/rss/cnn_allpolitics.rss");
                                    break;
                                case "Technology":
                                    new GetArticleAsync(MainActivity.this,progressDialog).execute("http://rss.cnn.com/rss/cnn_tech.rss");
                                break;
                                case "Health":
                                    new GetArticleAsync(MainActivity.this,progressDialog).execute("http://rss.cnn.com/rss/cnn_health.rss");
                                    break;
                                case "Entertainment":
                                    new GetArticleAsync(MainActivity.this,progressDialog).execute("http://rss.cnn.com/rss/cnn_showbiz.rss");
                                    break;
                                case "Travel":
                                    new GetArticleAsync(MainActivity.this,progressDialog).execute("http://rss.cnn.com/rss/cnn_travel.rss");
                                    break;
                                case "Living":
                                    new GetArticleAsync(MainActivity.this,progressDialog).execute("http://rss.cnn.com/rss/cnn_living.rss");
                                break;
                                case "Most Recent":
                                    new GetArticleAsync(MainActivity.this,progressDialog).execute("http://rss.cnn.com/rss/cnn_latest.rss");
                                    break;
                            }
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
        final Article eachArt= news.get(index);
        tvTitle.setText(eachArt.getTitle());
        tvPublish.setText(eachArt.getPublishedAt());
        //tvDesc.setText(eachArt.getDescription().equals("null")?"" :eachArt.getDescription());
       // String[] a = eachArt.getDescription().split("&lt");
        tvDesc.setText(eachArt.getDescription().equals("null")?"" :eachArt.getDescription().split("<")[0]);
        tvCount.setText(index+1+" out of "+news.size());

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(eachArt.getLink()));
                startActivity(browserIntent);
            }
        });

        ivMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(eachArt.getLink()));
                startActivity(browserIntent);
            }
        });

        if(eachArt.getUrlToImg()!=null && !eachArt.getUrlToImg().equalsIgnoreCase("null")){
            Picasso.with(MainActivity.this).load(eachArt.getUrlToImg()).into(ivMainImage);
        }else{
            ivMainImage.setImageResource(R.drawable.noimage);
            //Toast.makeText(MainActivity.this, "No Image Found", Toast.LENGTH_SHORT).show();
        }
    }
}
