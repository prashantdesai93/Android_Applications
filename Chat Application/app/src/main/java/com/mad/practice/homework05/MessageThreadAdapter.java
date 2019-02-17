package com.mad.practice.homework05;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hp on 4/7/2018.
 */

public class MessageThreadAdapter extends ArrayAdapter<Thread> {

    private final OkHttpClient client = new OkHttpClient();
    private final Context context;
    SharedPreferences prefs;
    Thread t;
    private RemoveThreadCallback callback;

    public void setCallback(RemoveThreadCallback callback){
        this.callback = callback;
    }

    public MessageThreadAdapter(@NonNull Context context, int resource, @NonNull List<Thread> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("demo", "Helloooooooooooooooooo");

        prefs = context.getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MessageThreadsActivity.MODE_PRIVATE);
        //AllMessageThreads allMessageThreads = getItem(position);
        t = getItem(position);
        Log.d("demo", "MessageThreadAdapter : getView: t = "+t.toString());
        //Log.d("demo", "getView: "+allMessageThreads.threads.get(0).title);
        ViewHolder viewHolder;
        if(convertView ==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.thread_item ,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvThreadTitle = convertView.findViewById(R.id.tvThreads);
            viewHolder.ivDeleteThread = convertView.findViewById(R.id.ivDeleteThread);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //for (int i = 0; i < allMessageThreads.threads.size(); i++) {
        Log.d("demo", "getView: "+t.title);
        viewHolder.tvThreadTitle.setText(t.title);
        viewHolder.ivDeleteThread.setTag(position);
        viewHolder.ivDeleteThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                // Access the row position here to get the correct data item
                Thread thread = getItem(position);
                if(callback != null) {
                    if(isConnected()){
                        callback.removeThread(thread);
                    } else{
                        Toast.makeText(context, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        if(t.user_id == prefs.getInt("userId", 0)){
            viewHolder.ivDeleteThread.setVisibility(View.VISIBLE);
        }else{
            viewHolder.ivDeleteThread.setVisibility(View.INVISIBLE);
        }
        //}

        return convertView;
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    private static class ViewHolder{
        TextView tvThreadTitle;
        ImageView ivDeleteThread;
    }

    public interface RemoveThreadCallback {

        public void removeThread(Thread t);
    }
}
