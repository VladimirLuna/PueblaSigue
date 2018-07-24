package com.vlim.puebla;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class DataParser {
    String TAG = "PUEBLA";
    Context context;

    private HashMap<String,String> getDuration(JSONArray googleDirectionsJson)
    {
        HashMap<String,String> googleDirectionsMap = new HashMap<>();
        String duration = "";
        String distance ="";

        try {

            duration = googleDirectionsJson.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = googleDirectionsJson.getJSONObject(0).getJSONObject("distance").getString("text");

            googleDirectionsMap.put("duration" , duration);
            googleDirectionsMap.put("distance", distance);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return googleDirectionsMap;
    }


    private HashMap<String, String> getPlace(JSONObject googlePlaceJson)
    {
        HashMap<String, String> googlePlacesMap = new HashMap<>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        Log.d(TAG, "getPlace. Entered");


        try {
            if(!googlePlaceJson.isNull("name"))
            {

                placeName = googlePlaceJson.getString("name");

            }
            if( !googlePlaceJson.isNull("vicinity"))
            {
                vicinity = googlePlaceJson.getString("vicinity");

            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            reference = googlePlaceJson.getString("reference");

            googlePlacesMap.put("place_name" , placeName);
            googlePlacesMap.put("vicinity" , vicinity);
            googlePlacesMap.put("lat" , latitude);
            googlePlacesMap.put("lng" , longitude);
            googlePlacesMap.put("reference" , reference);


            Log.d(TAG, "getPlace. Putting Places");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googlePlacesMap;
    }



    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray)
    {
        int count = jsonArray.length();
        List<HashMap<String,String>> placesList = new ArrayList<>();
        HashMap<String,String> placeMap = null;
        Log.d(TAG, "Places. getPlaces");

        for(int i = 0;i<count;i++)
        {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;

    }

    public List<HashMap<String,String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            Log.d(TAG, "parse");

            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }

    public String[] parseDirections(String jsonData, Context contextActivity)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Obtiene segmentos de ruta y almacena en bd
        Log.d(TAG, "Segmentos: " + jsonArray.length());
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectLegs = jsonArray.getJSONObject(i);
                //Log.d(TAG,"jsonObjectLegs: " + jsonObjectLegs.getString("end_location"));   // [0]
                //String end_location = jsonObjectLegs.getString("lat");

                JSONObject start_location = jsonObjectLegs.getJSONObject("start_location");
                JSONObject end_location = jsonObjectLegs.getJSONObject("end_location");

                String lat1 = String.valueOf(start_location.get("lat"));
                String lng1 = String.valueOf(start_location.get("lng"));
                String lat2 = String.valueOf(end_location.get("lat"));
                String lng2 = String.valueOf(end_location.get("lng"));

                //Log.d(TAG, "Segmentos: " + lat1 + ", " + lng1 + " } " + lat2 + ", " + lng2);

                //  ---------------- BD ------------------------------------------------------------------
                userSQLiteHelper usdbh =
                        new userSQLiteHelper(contextActivity, "DBUsuarios", null, Config.VERSION_DB);
                SQLiteDatabase db = usdbh.getWritableDatabase();

                if (db != null) {
                    //Insertamos los datos en la tabla Usuarios
                    db.execSQL("INSERT INTO RutaSigueme (lat1, lng1, lat2, lng2) VALUES ('" + lat1 + "', '" + lng1 + "', '" + lat2 + "', '" + lng2 + "')");
                    //Log.d(TAG, "Ruta guardada OK");
                    db.close();
                }
                else {
                    Log.v(TAG, "No Hay base en donde guardar!");
                }


                // ----------------------------------------------------------------------------------------

                //String value = jsonArray.getString(i);
                //String end_location = jsonObjectLegs.end_location[0];
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }



        ///
        return getPaths(jsonArray);
    }

    public String[] getPaths(JSONArray googleStepsJson )
    {
        int count = googleStepsJson.length();
        String[] polylines = new String[count];

        for(int i = 0;i<count;i++)
        {
            try {
                polylines[i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return polylines;
    }

    public String getPath(JSONObject googlePathJson)
    {
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }
}
