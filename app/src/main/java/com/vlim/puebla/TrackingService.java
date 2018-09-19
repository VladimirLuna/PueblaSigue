package com.vlim.puebla;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
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
    private static final int LOCATION_INTERVAL = 10000; // 1 minuto 60000      5 minutos 300000ms
    private static final float LOCATION_DISTANCE = 100f;
    int contador = 0;
    String idviaje = "0", nombreUsr;
    String lastLatitude, lastLongitude;
    JSONArray jsonArr;
    String JsonResponse = null;
    Context context;
    String idusuario;

    final static String ACTION = "NotifyServiceAction";
    final static String STOP_SERVICE = "";
    final static int RQS_STOP_SERVICE = 1;
    private static final int MY_NOTIFICATION_ID=1;
    private NotificationManager notificationManager;
    private Notification myNotification;

    NotifyServiceReceiver notifyServiceReceiver;


    private class LocationListener implements android.location.LocationListener{
        Location mLastLocation;
        public LocationListener(String provider)
        {
            Log.i(TAG, "LocationListener, provider: " + provider);
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

            bufferRuta();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG, "onStatusChanged, provider: " + provider + ", status: " + status);

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
        Log.d(TAG, "Buffer Ruta");
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
                            Toast.makeText(getApplicationContext(), "Ha salido de la ruta, enviando mensaje a contactos de emergencia", Toast.LENGTH_LONG).show();
                            muestraNotificacion();

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
                            //String telefono = jsonObj.getString("telefono");
                            String celular = jsonObj.getString("celular");
                            //String correo_contacto = jsonObj.getString("correo_contacto");

                            Log.i(TAG, "info contacto nombre: " + nombre_completo + ", tel: " + celular);

                            String mensajeSMS = "Alerta: " + nombreUsr + " ha salido de su ruta. Su última ubicación es: http://maps.google.com/maps?saddr=" + lastLatitude + "," + lastLongitude;
                            Log.d(TAG, "sms: " + mensajeSMS);


                            PendingIntent sentPI = PendingIntent.getBroadcast(getApplicationContext(), 0,
                                    new Intent("SMS_SENT"), 0);

                            /*PendingIntent piEnvio   = PendingIntent.getBroadcast(this,0,new Intent(SENT_BROADCAST),0);
                            PendingIntent piEntrega = PendingIntent.getBroadcast(this,0,new Intent(DELIVERED_BROADCAST),0);*/

                            try{
                                SmsManager sms = SmsManager.getDefault();
                                sms.sendTextMessage(celular, null, mensajeSMS, null, null);
                            }
                            catch (Exception e) {
                                Log.e(TAG, "SMS faild, please try again. " + e.getMessage());
                                e.printStackTrace();
                            }

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

    public class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            int rqs = arg1.getIntExtra("RQS", 0);
            if (rqs == RQS_STOP_SERVICE) {
                stopSelf();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Start Service");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.amu_bubble_mask);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Mujer Segura Puebla")
                .setTicker("Sígueme y cuídame")
                .setContentText("Sígueme y cuídame activo")
                .setSmallIcon(R.drawable.amu_bubble_mask)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(Notification.FLAG_ONGOING_EVENT,notification);



        //super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        notifyServiceReceiver = new NotifyServiceReceiver();
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

    public void muestraNotificacion() {

        Log.d(TAG, "Enviando notificación");

        Context context = getApplicationContext();
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle("Latest Coupon");
        notificationBuilder.setContentText("Get Upto 10% Off on Mobiles");

        Intent notificationClickIntent = new Intent(this, NotificationActionActivity.class);
        PendingIntent notificationPendingIntent =
                PendingIntent.getActivity(context, 0, notificationClickIntent, 0);

        notificationBuilder.setContentIntent(notificationPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, notificationBuilder.build());


    }
}
