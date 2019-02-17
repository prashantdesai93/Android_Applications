package com.mad.practice.hw03;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hp on 2/12/2018.
 */

public class GetImageAsync extends AsyncTask<String, Void, Void> {
    ImageView imageView;
    ProgressBar progressBar;
    Bitmap bitmap = null;
    int cancelled=0;

    public GetImageAsync(ImageView imageView, ProgressBar progressBar) {
        this.imageView = imageView;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        //progressDialog.setMessage("Loading");
       // progressDialog.setCancelable(false);
       // progressDialog.show();
        imageView.setImageBitmap(null);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection hConn = null;
        //Bitmap image = null;
        while(!isCancelled()){
            try {
                URL url  = new URL(params[0]);
                hConn = (HttpURLConnection) url.openConnection();
                bitmap = BitmapFactory.decodeStream(hConn.getInputStream());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(hConn != null)
                    hConn.disconnect();
            }
            return null;
        }
        cancelled=-1;
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
       // progressDialog.dismiss();
        progressBar.setVisibility(View.GONE);
        if(cancelled!= -1){
            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

    }
}

