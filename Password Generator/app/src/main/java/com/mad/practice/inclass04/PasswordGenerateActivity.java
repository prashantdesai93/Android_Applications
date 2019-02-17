package com.mad.practice.inclass04;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PasswordGenerateActivity extends AppCompatActivity {

    Handler handler;
    ProgressDialog progressDialog;
    TextView tvPasswordValue;
    ExecutorService threadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_generate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating Passwords");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        tvPasswordValue = findViewById(R.id.tvPasswordValue);
        SeekBar sb = findViewById(R.id.sbPassCount);

        final TextView tvPassCount = findViewById(R.id.tvPassCount);

        threadPool = Executors.newFixedThreadPool(1);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPassCount.setText(1+progress+ "");
                Log.d("demo", tvPassCount.getText().toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar sb2 = findViewById(R.id.sbPassLength);

        final TextView tvPassLength = findViewById(R.id.tvPassLength);
        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPassLength.setText(8+progress+"");
                Log.d("demo", tvPassCount.getText().toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch(msg.what){
                    case GeneratePassword.STATUS_START:
                        progressDialog.show();
                        Log.i("handleMessage","Starting......");
                        break;
                    case GeneratePassword.STATUS_PROGRESS:
                        progressDialog.setProgress(msg.getData().getInt(GeneratePassword.PROGRESS_KEY));
                        Log.i("handleMessage","Progress......"+msg.getData().getInt(GeneratePassword.PROGRESS_KEY));
                        break;
                    case GeneratePassword.STATUS_STOP:
                        progressDialog.dismiss();
                        final String [] receivedPasswords = msg.getData().getStringArray(GeneratePassword.PASSWORDS_KEY);

                        AlertDialog.Builder builder = new AlertDialog.Builder(PasswordGenerateActivity.this);
                        builder.setTitle("Alert Dialog")
                                .setCancelable(false)
                            .setItems(receivedPasswords, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d("demo", (String) receivedPasswords[which]);
                                    tvPasswordValue.setText(receivedPasswords[which]);
                                }
                            });
                        AlertDialog ad = builder.create();
                        ad.show();
                        Log.i("handleMessage","Stopping......");
                        break;
                }

                return false;
            }
        });

        findViewById(R.id.btnThread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int noOfPasswords=Integer.parseInt(tvPassCount.getText().toString());
                int lengthOfPassword=Integer.parseInt(tvPassLength.getText().toString());
                threadPool.execute(new GeneratePassword(lengthOfPassword,noOfPasswords));
                //new Thread(new GeneratePassword(lengthOfPassword,noOfPasswords),"Generate Password").start();
            }
        });

        findViewById(R.id.btnAsync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int noOfPasswords=Integer.parseInt(tvPassCount.getText().toString());
                int lengthOfPassword=Integer.parseInt(tvPassLength.getText().toString());

                new GeneratePasswordAsync(lengthOfPassword,noOfPasswords).execute();
            }
        });
    }

    class GeneratePasswordAsync extends AsyncTask<Integer,Integer,String []> {

        int lengthOfPassword;
        int noOfPassword;
        String [] passwords ;

        public GeneratePasswordAsync(int lengthOfPassword, int noOfPassword) {
            this.lengthOfPassword = lengthOfPassword;
            this.noOfPassword = noOfPassword;
            this.passwords= new String [noOfPassword];
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(PasswordGenerateActivity.this);
            progressDialog.setMessage("Generating Passwords Async");
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected void onPostExecute(final String [] receivedPasswords) {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(PasswordGenerateActivity.this);
            builder.setTitle("Alert Dialog")
                    .setCancelable(false)
                    .setItems(receivedPasswords, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("demo", (String) receivedPasswords[which]);
                            tvPasswordValue.setText(receivedPasswords[which]);
                        }
                    });
            AlertDialog ad = builder.create();
            ad.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected String [] doInBackground(Integer... params) {
            for(int i =0;i<noOfPassword;i++){
                passwords[i]=Util.getPassword(lengthOfPassword);
                int progress = (int )((i+1)*100)/noOfPassword;
                Log.i("prog",progress+"");
                publishProgress(progress);
            }
            return passwords;
        }
    }

    public class GeneratePassword implements Runnable{

        static final int STATUS_START=0x00;
        static final int STATUS_PROGRESS=0x01;
        static final int STATUS_STOP=0x02;
        static final String PROGRESS_KEY="PROGRESS";
        static final String PASSWORDS_KEY="PASSWORDS";

        int lengthOfPassword;
        int noOfPassword;
        String [] passwords ;

        public GeneratePassword(int lengthOfPassword, int noOfPassword) {
            this.lengthOfPassword = lengthOfPassword;
            this.noOfPassword = noOfPassword;
            this.passwords= new String [noOfPassword];
        }

        @Override
        public void run() {
            Log.i("GeneratePassword Run","Started work......");
            Message startMessage = new Message();
            startMessage.what=STATUS_START;
            handler.sendMessage(startMessage);


            for(int i =0;i<noOfPassword;i++){

                passwords[i]=Util.getPassword(lengthOfPassword);
                Message progressMessage = new Message();
                progressMessage.what=STATUS_PROGRESS;
                Bundle bundle = new Bundle();
                int progress = (int )((i+1)*100)/noOfPassword;
                bundle.putInt(PROGRESS_KEY,progress);
                Log.i("prog",progress+"");
                progressMessage.setData(bundle);
                handler.sendMessage(progressMessage);
            }

            Message stopMessage = new Message();
            stopMessage.what=STATUS_STOP;
            Bundle passwordsBundle = new Bundle();
            passwordsBundle.putStringArray(PASSWORDS_KEY,passwords);
            stopMessage.setData(passwordsBundle);
            handler.sendMessage(stopMessage);
            Log.i("GeneratePassword Run","Finished work......");
        }
    }
}
