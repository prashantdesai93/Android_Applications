package com.mad.practice.inclass05;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by hp on 2/12/2018.
 */

public class GetImageAsync extends AsyncTask<String, Void, Void> {
    ImageView imageView;
    ProgressDialog progressDialog;
    Bitmap bitmap = null;

    public GetImageAsync(ImageView imageView, ProgressDialog progressDialog) {
        this.imageView = imageView;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection hConn = null;
        //Bitmap image = null;
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

    @Override
    protected void onPostExecute(Void aVoid) {
        progressDialog.dismiss();
        if (bitmap != null && imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}

