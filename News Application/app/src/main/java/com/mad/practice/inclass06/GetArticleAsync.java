package com.mad.practice.inclass06;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kiran on 2/19/2018.
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

    @Override
    protected ArrayList<Article> doInBackground(String... strings) {
        HttpURLConnection connection = null;
        ArrayList<Article> result = new ArrayList<>();
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String json = IOUtils.toString(connection.getInputStream());
                JSONObject root = new JSONObject(json);
                JSONArray articles = root.getJSONArray("articles");
                for(int i=0; i< articles.length(); i++){
                    JSONObject articleJson = articles.getJSONObject(i);

                    Article article = new Article();
                    article.setTitle(articleJson.getString("title"));
                    article.setDescription(articleJson.getString("description"));
                    article.setUrlToImg(articleJson.getString("urlToImage"));
                    article.setPublishedAt(articleJson.getString("publishedAt"));

                    result.add(article);
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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