package com.mad.practice.inclass05;

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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GetKeyWordsAsync.IData,GetUrlsAsync.IData{
    List<String> keyWordsList = new ArrayList<String>();
    String [] items ;
    EditText etSearch;
    ImageView ivMainImage;
    ProgressDialog progressDialog;
    List<String> urls = new ArrayList<>();
    int count=0;
    ImageButton ibNextBtn;
    ImageButton ibPrevBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etSearch = findViewById(R.id.etSearch);
        ivMainImage = findViewById(R.id.ivMainImage);
        progressDialog = new ProgressDialog(MainActivity.this);
        if(isConnected()){
            new GetKeyWordsAsync(MainActivity.this).execute("http://dev.theappsdr.com/apis/photos/keywords.php");
        }else{
            Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
        }

        ibPrevBtn = findViewById(R.id.ibPrev);
        ibPrevBtn.setClickable(false);
        ibPrevBtn.setEnabled(false);

        ibNextBtn = findViewById(R.id.ibNext);
        ibNextBtn.setClickable(false);
        ibNextBtn.setEnabled(false);

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
    public void handleKeyWords(final List<String> data) {
        Log.d("handleKeyWords", data.toString()+""+data.size() );
        keyWordsList = data;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Keyword")
                .setItems(data.toArray(new CharSequence[data.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etSearch.setText(data.get(which));
                        count=0;
                        if(isConnected()){
                            new GetUrlsAsync(MainActivity.this).execute("http://dev.theappsdr.com/apis/photos/index.php?keyword="+data.get(which));
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
    }

    @Override
    public void handleUrls(List<String> data) {
        if(data.size() > 1){
            ibPrevBtn.setClickable(true);
            ibPrevBtn.setEnabled(true);
            ibNextBtn.setClickable(true);
            ibNextBtn.setEnabled(true);
        }
        if(data!=null && data.size()>0){
            Log.i("handleUrls", data.toString());
            urls=data;
            if(isConnected()){
                new GetImageAsync(ivMainImage,progressDialog).execute(data.get(0));
            }else{
                Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        }else{
            urls=data;
            ivMainImage.setImageResource(android.R.color.transparent);
            Toast.makeText(MainActivity.this, "No Images Found", Toast.LENGTH_SHORT).show();

            ibPrevBtn.setClickable(false);
            ibPrevBtn.setEnabled(false);
            ibNextBtn.setClickable(false);
            ibNextBtn.setEnabled(false);
        }

    }

    public void nextImage(){
        count++;
        if(count == urls.size()){
            count = 0;
        }
        if(count >-1 && urls.size()>0){
            if(isConnected()){
                new GetImageAsync(ivMainImage,progressDialog).execute(urls.get(count%urls.size()));
            }else{
                Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void prevImage(){
        count--;
        if(count == -1 && urls.size()>0){
            count = urls.size()-1;
        }
        if(urls.size()>0 && count >-1 && count<urls.size()){
            if(isConnected()){
                new GetImageAsync(ivMainImage,progressDialog).execute(urls.get(count));
            }else{
                Toast.makeText(MainActivity.this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
