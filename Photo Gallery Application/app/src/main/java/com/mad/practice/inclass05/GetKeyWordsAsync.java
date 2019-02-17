package com.mad.practice.inclass05;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kiran on 2/12/2018.
 */

public class GetKeyWordsAsync extends AsyncTask<String, Void, String> {

    IData iData;

    public GetKeyWordsAsync(IData iData) {
        this.iData = iData;
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection=null;
        BufferedReader reader=null;
        StringBuilder sb = new StringBuilder();
        //List keyWords = new ArrayList<String>();
        String result=null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                result = IOUtils.toString(connection.getInputStream());
            }
             /*reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line="";
                while((line =reader.readLine())!=null){
                    //sb.append(line);
                    keyWords.add(line);
                }*/
                //result = sb.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(connection!=null){
                connection.disconnect();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s!=null){
            Log.d("onPostExecute", s.toString());
            List<String> sList = new ArrayList<String>();
            for(String eachS :s.split(";") )
            sList.add(eachS);
            iData.handleKeyWords(sList);
        }else{
            Log.d("onPostExecute", "No Result");
        }
    }

    public static interface IData{
        public void handleKeyWords(List<String> data);
    }
}
