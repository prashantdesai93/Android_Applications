package com.mad.practice.inclass11;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    String pageData[];          //Stores the text to swipe.
    LayoutInflater inflater;    //Used to create individual pages
    ViewPager vp;
    EditText etSearch;
    Button btnSearch ;
    private final OkHttpClient client = new OkHttpClient();
    List<URls> imgUrls =null;
    Handler h;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imgUrls = new ArrayList<URls>();
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);

        //Get the data to be swiped through
        //pageData=getResources().getStringArray(R.array.desserts);
        //get an inflater to be used to create single pages
        inflater = (LayoutInflater) getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);
        //Reference ViewPager defined in activity
        vp=(ViewPager)findViewById(R.id.viewPager);
        //set the adapter that will create the individual pages
        //vp.setAdapter(new MyPagesAdapter());

        progressDialog = new ProgressDialog(MainActivity.this);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etSearch.getText().length()>0){
                    String searchKey = etSearch.getText().toString();

                    if(isConnected()) {
                        progressDialog.show();
                        loadPhotos(searchKey);
                    } else{
                        Toast.makeText(MainActivity.this, "Please connect to the Internet.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Please Enter Key Word " +
                            "to Search for images", Toast.LENGTH_SHORT).show();
                }
            }
        });

        h = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what == 0){
                    vp.setAdapter(new MyPagesAdapter());
                    progressDialog.dismiss();
                }else if(msg.what == 1){
                    progressDialog.dismiss();
                    printToast();
                }else{
                    progressDialog.dismiss();
                }
            }
        };
    }

    public void printToast(){
        Toast.makeText(getApplicationContext(), "Image not available, try with new keyword", Toast.LENGTH_SHORT).show();
        vp.setAdapter(null);
        etSearch.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear) {
            //Toast.makeText(this, "clear clicked", Toast.LENGTH_SHORT).show();
            vp.setAdapter(null);
            etSearch.setText("");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void loadPhotos(String searchKey){

        Log.d("demo", "LoadPhotos");

        String url = formUrl(searchKey);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                Log.d("demo", "MainActivity loadPhotos : onResponse: "+responseString);

                try {
                    Gson gson = new Gson();
                    ImageUrl allUrls = gson.fromJson(responseString, ImageUrl.class);

                    Log.d("demo", "MainActivity : onResponse: Urls " + allUrls.hits);
                    imgUrls = allUrls.hits;
                /*imgUrls.toArray();
                pageData=getResources().getStringArray(R.array.desserts);*/

                    if (allUrls.hits.size() > 0) {
                        Message message = new Message();
                        message.what = 0;
                        h.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = 1;
                        h.sendMessage(message);
                    }
                } catch(Exception e){
                    Message message = new Message();
                    message.what = 2;
                    h.sendMessage(message);
                }
            }
        });
    }

    public String formUrl(String searchKey){
        return "https://pixabay.com/api/?key=8642344-783389c8d1b0679b76c39470d&q="+searchKey+"&image_type=photo&pretty=true";
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    class MyPagesAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            //Return total pages, here one for each data item
            //Log.d("demo", "getCount: "+imgUrls.size());
            return imgUrls.size();
        }
        //Create the given page (indicated by position)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = inflater.inflate(R.layout.page, null);
            //((TextView)page.findViewById(R.id.textMessage)).setText(pageData[position]);
            ImageView ivPhotos = page.findViewById(R.id.ivPhotos);
            Log.d("demo", "getCount: "+imgUrls.size());
            Log.d("demo", "getCount: "+imgUrls.get(position).largeImageURL);
            Picasso.with(MainActivity.this).load(imgUrls.get(position).largeImageURL).into(ivPhotos);
            //Add the page to the front of the queue
            ((ViewPager) container).addView(page, 0);
            return page;
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            //See if object from instantiateItem is related to the given view
            //required by API
            return arg0==(View)arg1;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
            object=null;
        }
    }
}


