package com.example.fueltrackv1;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;

public class NearByPlaces extends AsyncTask<Object, String, String >
{
    private String googlePlaceData, url;
    private GoogleMap mMap;


    @Override
    protected String doInBackground(Object... objects)
    {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData=downloadUrl.ReadtheUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearByPlaceList = null;
        dataParser dataparser = new dataParser();
        try {
            nearByPlaceList = dataparser.parse(s);
            DisplayNearByPlaces(nearByPlaceList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void DisplayNearByPlaces(List<HashMap<String, String>> nearByPlaceList)
    {
        for(int i=0;i<nearByPlaceList.size();i++)
        {

            HashMap<String, String> googleNearByPlace = nearByPlaceList.get(i);
            String nameofPlace = googleNearByPlace.get("place_name");
            String vicinity = googleNearByPlace.get("vicinity");
            Double lat = Double.parseDouble(googleNearByPlace.get("lat"));
            Double lng = Double.parseDouble(googleNearByPlace.get("lng"));

            LatLng latLng = new LatLng(lat, lng);
            MarkerOptions options = new MarkerOptions().position(latLng).title(nameofPlace+ ":" +vicinity);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
            mMap.addMarker(options);
        }
    }
}
