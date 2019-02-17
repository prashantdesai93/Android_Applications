package com.mad.practice.inclass05;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiran on 2/12/2018.
 */

public class GetUrlsAsync extends AsyncTask<String, Void, List<String>> {

    IData iData;

    public GetUrlsAsync(IData iData) {
        this.iData = iData;
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        HttpURLConnection connection=null;
        BufferedReader reader=null;
        List urls = new ArrayList<String>();
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
             reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line="";
                while((line =reader.readLine())!=null){
                    urls.add(line);
                }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(connection!=null){
                connection.disconnect();
            }
        }
        return urls;
    }

    @Override
    protected void onPostExecute(List<String> urls) {
        if(urls!=null){
            Log.d("onPostExecute", urls.toString());
            iData.handleUrls(urls);
        }else{
            Log.d("onPostExecute", "No Result");
        }
    }

    public static interface IData{
        public void handleUrls(List<String> data);
    }
}
