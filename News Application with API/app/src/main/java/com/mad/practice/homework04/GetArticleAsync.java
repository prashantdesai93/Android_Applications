package com.mad.practice.homework04;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hp on 2/22/2018.
 */

public class GetArticleAsync extends AsyncTask<String, Void, ArrayList<Article>> {

    IData idata;
    ProgressDialog progressDialog;

    public GetArticleAsync(IData idata,ProgressDialog progressDialog) {
        this.idata = idata;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.show();
    }

    public GetArticleAsync() {
    }

    @Override
    protected ArrayList<Article> doInBackground(String... strings) {
        HttpURLConnection connection = null;
        ArrayList<Article> result = new ArrayList<>();
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                result = ArticleParser.NewsParser.parseNews(connection.getInputStream());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Article> s) {
        progressDialog.dismiss();
        if(s != null){
            Log.d("onPostExecute Result ", s.toString());
            idata.handleArticles(s);
        } else {
            Log.d("onPostExecute", "No Result");
        }
    }
    public static interface IData{
        public void handleArticles(ArrayList<Article> articles);
    }
}