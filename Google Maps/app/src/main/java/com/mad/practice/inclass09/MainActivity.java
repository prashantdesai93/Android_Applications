package com.mad.practice.inclass09;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    protected GeoDataClient mGeoDataClient;
    LocationManager locationManager;
    PolylineOptions pOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGeoDataClient = Places.getGeoDataClient(this, null);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        pOptions = new PolylineOptions();
        pOptions.geodesic(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
            return;
        } else {

        }

            String con = readFile(R.raw.trip);

            Loc result = convertToGson(con);

            Log.d("demo", "onMapReady: "+result);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

            LatLngBounds.Builder builder = LatLngBounds.builder();


            LatLng latLng=null;
            for (Loc.Point point:result.points) {
                latLng = new LatLng(point.latitude, point.longitude);
                pOptions.add(latLng);
                builder.include(latLng);
            }



            mMap.addMarker(new MarkerOptions().position(new LatLng(result.points.get(0).latitude, result.points.get(0).longitude))).setTitle("Start Location");
            mMap.addMarker(new MarkerOptions().position(new LatLng(result.points.get(result.points.size()-1).latitude, result.points.get(result.points.size()-1).longitude))).setTitle("End Location");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(result.points.get(result.points.size()-1).latitude, result.points.get(result.points.size()-1).longitude), 9.8f));

            mMap.addPolyline(pOptions);

            final LatLngBounds bounds = builder.build();

            try{
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, 50));
            } catch (Exception e){
                e.printStackTrace();
            }

    }


    public String readFile(int  id){
        //Get the text file
        //File file = new File(getResources().openRawResource(R.raw.trip));

        //Read text from file
        StringBuilder text = new StringBuilder();
        BufferedReader br=null;
        try {
            br= new BufferedReader(new InputStreamReader(getResources().openRawResource(id)));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        }
        catch (IOException e) {
            Log.d("demo", "MainActivity : readFile: Exception Occured"+e.getMessage());
            e.printStackTrace();
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return text.toString();
    }

    public Loc convertToGson(String str){
        Gson gson = new Gson();
        Loc loc = gson.fromJson(str,Loc.class);

        Log.d("demo", "MainActivity : convertToGson: "+loc.toString());

        return loc;
    }
}
