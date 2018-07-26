package com.vlim.puebla;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;

public class GetDirectionsData extends AsyncTask<Object,String,String> {
    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration, distance, colorRuta;
    LatLng latLng, latLng_Ini;
    double latitude_ini, lng_ini, latitude_dest, lng_dest;
    String TAG = "PUEBLA";
    Context context;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        latLng = (LatLng)objects[2];
        latitude_ini = (Double)objects[3];
        lng_ini = (Double)objects[4];
        latitude_dest = (Double)objects[5];
        lng_dest = (Double)objects[6];
        context = (Context) objects[7];
        colorRuta = (String) objects[8];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleDirectionsData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String paramsRoute) {
        Log.d(TAG, "Ruta: " + paramsRoute);
        String[] directionsList;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(paramsRoute, context);
        displayDirection(directionsList);
    }

    public void displayDirection(String[] directionsList)
    {
        mMap.clear();
        int count = directionsList.length;
        for(int i = 0;i<count;i++)
        {
            PolylineOptions options = new PolylineOptions();
            if(colorRuta.equals("naranja")){
                options.color(Color.parseColor("#ea7e01"));
            }
            else if(colorRuta.equals("azul")){
                options.color(Color.parseColor("#0262e5"));
            }
            options.width(20);
            options.addAll(PolyUtil.decode(directionsList[i]));
            //Log.d(TAG, "directionsList: " + PolyUtil.decode(directionsList[i]));
            mMap.addPolyline(options);
        }

        Log.d(TAG, "pinta ruta!");

        latLng_Ini = new LatLng(latitude_ini, lng_ini);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLng_Ini);
        builder.include(latLng);
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200);
        mMap.animateCamera(cu);

    }
}
