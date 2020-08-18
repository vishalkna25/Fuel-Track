package com.example.fueltrackv1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class dataParser
{
    private HashMap<String, String> getSingleNearByPlace(JSONObject googlePlaceJSON) throws JSONException {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String NameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";

        if(!googlePlaceJSON.isNull("name"))
        {
            NameOfPlace = googlePlaceJSON.getString("name");
        }
        if(!googlePlaceJSON.isNull("vicinity"))
        {
            vicinity = googlePlaceJSON.getString("vicinity");
        }
        latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
        longitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
        reference = googlePlaceJSON.getString("reference");

        googlePlaceMap.put("place_name",NameOfPlace);
        googlePlaceMap.put("vicinity",vicinity);
        googlePlaceMap.put("lat",latitude);
        googlePlaceMap.put("lng",longitude);
        googlePlaceMap.put("reference",reference);

        return googlePlaceMap;
    }

    private List<HashMap<String, String>> getAllNearByPlaces(JSONArray jsonArray) throws JSONException {
        int counter = jsonArray.length();

        List<HashMap<String, String>> NearByPlaceList = new ArrayList<>();

        HashMap<String, String> NearByPlaceMap = null;

        for(int i=0;i<counter;i++)
        {
            NearByPlaceMap = getSingleNearByPlace((JSONObject) jsonArray.get(i));
            NearByPlaceList.add(NearByPlaceMap);
        }
        return NearByPlaceList;
    }

    public List<HashMap<String ,String>> parse(String JSONdata) throws JSONException {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        jsonObject = new JSONObject(JSONdata);
        jsonArray = jsonObject.getJSONArray("results");

        return getAllNearByPlaces(jsonArray);
    }
}
