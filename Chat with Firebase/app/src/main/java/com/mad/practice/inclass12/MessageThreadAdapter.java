package com.mad.practice.inclass12;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by hp on 4/7/2018.
 */

public class MessageThreadAdapter extends ArrayAdapter<Thread> {

    private final Context context;
    Thread t;
    private RemoveThreadCallback callback;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    List<Thread> objects;

    public void setCallback(RemoveThreadCallback callback){
        this.callback = callback;
    }

    public MessageThreadAdapter(@NonNull Context context, int resource, @NonNull List<Thread> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        Log.d("demo", "Helloooooooooooooooooo");
        //AllMessageThreads allMessageThreads = getItem(position);
        t = getItem(position);
//        Log.d("demo", "MessageThreadAdapter : getView: t = "+t.toString());
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
//        Log.d("demo", "getView: "+t.title);
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
                        //objects.remove(position);
                        //notifyDataSetChanged();
                        callback.removeThread(thread);
                    } else{
                        Toast.makeText(context, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        if(t.userId.equals(user.getUid())){
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
