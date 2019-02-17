package com.mad.practice.hw03;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2/14/2018.
 */

public class GetQuestionsAsync extends AsyncTask<String, Void, String> {

    IData iData;
    ProgressBar progressBar;
    ImageView ivWelcome;


    public GetQuestionsAsync(IData iData, ProgressBar progressBar, ImageView ivWelcome) {
        this.iData = iData;
        this.progressBar = progressBar;
        this.ivWelcome = ivWelcome;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection=null;
        String result=null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if(connection.getResponseCode()==HttpURLConnection.HTTP_OK){
                result = IOUtils.toString(connection.getInputStream());
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
        return result;
    }


    protected ArrayList<Question> createQuestionList(String allQuestions){
        ArrayList<Question> questions = new ArrayList<>();
        for(String eachS :allQuestions.split("\n") )
            questions.add(createQuestion(eachS));
        return questions;
    }

    protected Question createQuestion(String eachQues){
        String [] splits = eachQues.split(";");
        return process(splits);
    }
    protected Question process(String[] splits){

        //Log.d("process", splits[0]+" "+splits[1]+" "+splits[2]+" "+splits[3]);

        Question q = new Question();
        if(splits!=null && splits[0].length()>1){
            q.setQuestionNumber(Integer.parseInt(splits[0]));
        }
        if(splits!=null && splits[1].length()>1){
            q.setQuestion(splits[1]);
        }
        if(splits!=null && splits[2].length()>1){
            q.setImgUrl(splits[2]);
        }
        if(splits!=null && splits[splits.length-1].length()>1){
            q.setAns(Integer.parseInt(splits[splits.length-1]));
        }
        List<String> options = new ArrayList<>();
        for(int i =3; i<splits.length-1;i++){
            //Log.d("for", "splits["+i+"]"+splits[i]);
            if(splits[i].length()>1){
                options.add(splits[i]);
            }
        }
        if(options!=null && options.size()>1){
            q.setOptions(options);
        }
        Log.i("process", ""+q);
        return q;
    }


    @Override
    protected void onPostExecute(String s) {
        if(s!=null){
            Log.d("onPostExecute", s.toString());
            ArrayList<Question> qList =createQuestionList(s);
            iData.handleQuestions(qList);
            if(ivWelcome!=null){
                ivWelcome.setImageResource(R.drawable.trivia);
            }
        }else{
            Log.d("onPostExecute", "No Result");
        }
    }

    public static interface IData{
        public void handleQuestions(ArrayList<Question> data);
    }
}
