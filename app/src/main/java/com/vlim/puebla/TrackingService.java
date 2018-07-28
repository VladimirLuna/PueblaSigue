package com.vlim.puebla;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class TrackingService extends Service {

    private static final String TAG = "PUEBLA";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 30000; // 5 minutos 300000
    private static final float LOCATION_DISTANCE = 100f;
    int contador = 0;
    String idviaje = "0", nombreUsr;
    String lastLatitude, lastLongitude;
    JSONArray jsonArr;
    String JsonResponse = null;
    Context context;
    String idusuario;
    SmsManager sms = SmsManager.getDefault();

    private class LocationListener implements android.location.LocationListener{
        Location mLastLocation;
        public LocationListener(String provider)
        {
            Log.i(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            contador++;
            Log.i(TAG, "onLocationChanged: " + location);
            /*Toast.makeText(getApplicationContext(), contador + " Provider: " + location.getProvider() + ", Lat: " + location.getLatitude() +
            ", lng: " + location.getLongitude(), Toast.LENGTH_LONG).show();*/
            mLastLocation.set(location);

            lastLatitude = String.valueOf(location.getLatitude());
            lastLongitude = String.valueOf(location.getLongitude());

            /*File gpsTxt = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.external_dir) + "/");
            File gpxfile = new File(gpsTxt, "tracking.txt");
            try{
                if(!gpsTxt.exists()){
                    System.out.println("We had to make a new file.");
                    gpsTxt.createNewFile();
                }
                FileWriter writer = null;
                try {
                    writer = new FileWriter(gpxfile, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if(location.getProvider().equals("gps")){
                        String fecha = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss", Locale.getDefault()).format(new Date());
                        writer.append(contador + "," + fecha + ", " + location.getProvider() + ", " + location.getLatitude() +
                                ", " + location.getLongitude() + ", " + location.getSpeed()*3.6 + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch(IOException e) {
                System.out.println("COULD NOT LOG!!");
            }*/

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged: " + provider + ", status: " + status);

            // preguntar si está dentro del buffer
            bufferRuta();

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i(TAG, "onProviderDisabled: " + provider);
        }
    }

    private void bufferRuta() {
        // lee idviaje
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        //Cursor c = db.query("Usuarios", campos, "idusuario=?", args, null, null, null);
        Cursor c = db.rawQuery("SELECT idviaje FROM RutaSigueme", null);

        if (c.moveToFirst()) {
            Log.v(TAG, "hay cosas");
            //do {
                idviaje = c.getString(0);
            //} while(c.moveToNext());
        }
        else{
            Log.v(TAG, "NO hay cosas");
        }
        db.close();
        //////

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            jsonBody.put("idviaje", idviaje);
            jsonBody.put("lat", lastLatitude);
            jsonBody.put("lng", lastLongitude);

            final String requestBody = jsonBody.toString();
            Log.d(TAG, "params buffer: " + requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.BUFFER_RUTA_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "VOLLEY response login: " + response);
                    //Toast.makeText(getApplicationContext(), "Buffer: " + response, Toast.LENGTH_LONG).show();
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = jsonArr.getJSONObject(0);
                        if(jsonObj.getString("respuesta").equals("Error")){
                            Toast.makeText(getApplicationContext(), "Ha salido de la ruta, avisar a contactos", Toast.LENGTH_LONG).show();
                            enviaSMSContactos();
                            borraRuta();
                            stopService(new Intent(getApplicationContext(), TrackingService.class));
                        }
                        else{
                            Log.d(TAG, "Sigue en ruta");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "VOLLEY: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Error en la conexión.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void enviaSMSContactos() {
        // Busca contactos de emergencia
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            // obtiene idusr
            userSQLiteHelper usdbh =
                    new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
            SQLiteDatabase db = usdbh.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT idusuario, nick, nombre FROM Usuarios", null);

            if (c.moveToFirst()) {
                Log.v(TAG, "hay cosas");
                //Recorremos el cursor hasta que no haya más registros
                do {
                    idusuario = c.getString(0);
                    nombreUsr = c.getString(2);
                } while(c.moveToNext());
            }
            else{
                Log.v(TAG, "NO hay cosas");
            }
            c.close();
            db.close();
            //////

            jsonBody.put("idusr", idusuario);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_CONTACTOS_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.i("VOLLEYresponse", String.valueOf(jsonArr));
                    Log.i(TAG, "VOLLEY response contactos: " + response);

                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = jsonArr.getJSONObject(i);
                            String id_usuario_contacto = jsonObj.getString("id_usuario_contacto");
                            String nombre_completo = jsonObj.getString("nombre_completo");
                            Log.i(TAG, id_usuario_contacto + ", " + nombre_completo);

                            obtieneDetalleContacto(id_usuario_contacto);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "VOLLEY: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Error en la conexión.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtieneDetalleContacto(String id_contacto) {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            jsonBody.put("idusr", id_contacto);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_INFO_CONTACTO_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                        Log.i(TAG, "response: " + jsonArr);

                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObj = null;
                            try {
                                jsonObj = jsonArr.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String nombre_completo = jsonObj.getString("nombre_completo");
                            String telefono = jsonObj.getString("telefono");
                            String celular = jsonObj.getString("celular");
                            String correo_contacto = jsonObj.getString("correo_contacto");

                            Log.i(TAG, "info contacto nombre: " + nombre_completo + ", tel: " + telefono);

                            String mensajeSMS = "Alerta: " + nombreUsr + " ha salido de su ruta.";
                            sms.sendTextMessage(celular, null, mensajeSMS, null, null);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "VOLLEY: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Error en la comunicación.", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void borraRuta() {
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        db.delete("RutaSigueme", null, null);
        db.close();
        Log.i(TAG, "Borrando bd RutaSigueme");
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Start Service");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }
}
