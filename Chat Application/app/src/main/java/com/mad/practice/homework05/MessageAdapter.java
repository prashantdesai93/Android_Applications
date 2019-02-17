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

import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hp on 4/8/2018.
 */

public class MessageAdapter extends ArrayAdapter<Messages> {

    private final OkHttpClient client = new OkHttpClient();
    private final Context context;
    SharedPreferences prefs;
    Messages m;
    private RemoveMessageCallback callback;

    public void setCallback(MessageAdapter.RemoveMessageCallback callback){
        this.callback = callback;
    }

    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<Messages> objects) {
        super(context, resource, objects);
        this.context = context;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("demo", "in a message screen");

        prefs = context.getSharedPreferences(MainActivity.SHARED_PREFS_NAME, MessageThreadsActivity.MODE_PRIVATE);
        //AllMessageThreads allMessageThreads = getItem(position);
        m = getItem(position);
        //Log.d("demo", "getView: "+allMessageThreads.threads.get(0).title);
        MessageAdapter.ViewHolder viewHolder;
        if(convertView ==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item ,parent,false);
            viewHolder = new MessageAdapter.ViewHolder();
            viewHolder.tvMessageLabel = convertView.findViewById(R.id.tvMessageLabel);
            viewHolder.tvNameMsg = convertView.findViewById(R.id.tvNameMsg);
            viewHolder.tvTime = convertView.findViewById(R.id.tvTime);
            viewHolder.ivTrash = convertView.findViewById(R.id.ivTrash);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (MessageAdapter.ViewHolder) convertView.getTag();
        }
        //for (int i = 0; i < allMessageThreads.threads.size(); i++) {
        Log.d("demo", "getView: "+m.message);
        viewHolder.tvMessageLabel.setText(m.message);
        viewHolder.tvNameMsg.setText(m.user_fname+" "+m.user_lname);

        PrettyTime p = new PrettyTime();

        Date inputDate, outputDate=null;
        String dateString=m.created_at;
        SimpleDateFormat convertToDate=new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        SimpleDateFormat newDateFormat=new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
        final long HOUR = 3600*1000;
        try
        {
            inputDate=convertToDate.parse(dateString);
            String formattedDateString=newDateFormat.format(inputDate);
            outputDate= new Date(newDateFormat.parse(formattedDateString).getTime()+8*HOUR);
            Log.d("demo", "getView: Final Date:"+outputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.tvTime.setText(p.format(outputDate));

        viewHolder.ivTrash.setTag(position);
        viewHolder.ivTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("demo", "onClick: trash");
                int position = (Integer) v.getTag();
                // Access the row position here to get the correct data item
                Messages m = getItem(position);
                if(callback != null) {
                    Log.d("demo", "onClick: "+m.toString());
                    if(isConnected()){
                        callback.removeMessage(m);
                    } else{
                        Toast.makeText(context, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if(m.user_id == prefs.getInt("userId", 0)){
            viewHolder.ivTrash.setVisibility(View.VISIBLE);
        }else{
            viewHolder.ivTrash.setVisibility(View.INVISIBLE);
        }

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
        TextView tvMessageLabel;
        TextView tvNameMsg;
        TextView tvTime;
        ImageView ivTrash;
    }

    public interface RemoveMessageCallback {

        public void removeMessage(Messages m);
    }
}
