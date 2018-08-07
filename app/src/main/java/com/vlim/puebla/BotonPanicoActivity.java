package com.vlim.puebla;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class BotonPanicoActivity extends AppCompatActivity implements LocationListener {

    String idusuario = "";
    String TAG = "PUEBLA";

    Geocoder geocoder;
    String bestProvider;
    List<Address> user = null;
    String latitud, longitud;
    JSONArray jsonArr;
    String JsonResponse = null;
    String responseString2 = null;
    long totalSize = 0;
    ProgressDialog progressDialog;
    String responseStringPanico = null;

    // 2
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    LocationManager locationManager;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();

    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    private ProgressBar progressBar;
    TextView tv_toolbar;
    ImageView btn_back;

    // valida ubicacion en puebla
    EstaEnPuebla estaEnPuebla;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boton_panico);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        Log.d(TAG, "inicia");
        progressBar = (ProgressBar) findViewById(R.id.progressBarPanico);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        // obtiene posicion
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        Intent i = getIntent();
        idusuario = i.getStringExtra("idusuario");
        //Toast.makeText(getApplicationContext(), "id: " + idusuario, Toast.LENGTH_LONG).show();

        tv_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_toolbar.setTypeface(tf);
        btn_back = findViewById(R.id.btn_back);

        if (!isGPS && !isNetwork) {
            Log.d(TAG, "Connection off");
            showSettingsAlert();
            getLastLocation();
        } else {
            Log.d(TAG, "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d(TAG, "Permission requests");
                    canGetLocation = false;
                }
            }
            // get location
            getLocation();
        }

        progressDialog = new ProgressDialog(BotonPanicoActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Enviando emergencia.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();


        estaEnPuebla = new EstaEnPuebla(getApplicationContext());
        if(estaEnPuebla.estaEnPuebla(Double.valueOf(latitud), Double.valueOf(longitud))){
            Log.d(TAG, "Está en Puebla");

            // Preparando envio
            preparaEnvioPanico(idusuario, latitud, longitud);

            // Enviando parametros boton panico
            // Get instance of Vibrator from current Context
            final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Enviando params

            if (vibrator.hasVibrator()) {
                final long[] pattern = {100, 30, 100, 30, 100, 200, 200, 30, 200, 30, 200, 200, 100, 30, 100, 30, 100};   /*SOS*/
                new Thread() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 5; i++) { //repeat the pattern 5 times
                            vibrator.vibrate(pattern, -1);
                            try {
                                Thread.sleep(2000); //the time, the complete pattern needs
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
            }
        }
        else{
            showAlert(Config.ESTA_EN_PUEBLA);
        }



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void preparaEnvioPanico(String idusuario, String latitud, String longitud) {
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("idusr" , idusuario);
            post_dict.put("lat", latitud);
            post_dict.put("long", longitud);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            Log.v(TAG, "postdic len: " + String.valueOf(post_dict));

            enviaBotonPanico(String.valueOf(post_dict));
        }
    }

    private void enviaBotonPanico(String params) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String IDusuario = jsonObject.getString("idusr");
            String Lat = jsonObject.getString("lat");
            String Lng = jsonObject.getString("long");

            jsonBody.put("idusr", IDusuario);
            jsonBody.put("lat", Lat);
            jsonBody.put("long", Lng);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.BOTON_PANICO_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG, "VOLLEY response PANICO unipol: " + response);
                    progressDialog.dismiss();

                    JSONArray jsonRespuesta = null;
                    try {
                        jsonRespuesta = new JSONArray(response);
                        String respuesta = jsonRespuesta.getJSONObject(0).getString("respuesta");
                        String mensaje = jsonRespuesta.getJSONObject(0).getString("mensaje");

                        if(respuesta.equals("OK")){
                            Log.i(TAG, "Respuesta OK");
                            showAlert("Su emergencia está siendo procesada");
                        }
                        else{
                            Log.i(TAG, "Respuesta ERROR");
                            showAlert("Error en el envio. Por favor intente de nuevo." );
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error envio: " + e.getMessage());
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "VOLLEY: " + error.toString());
                    progressDialog.dismiss();
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

    private class UploadPanico extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            //super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

        }

        protected String doInBackground(String... params) {
            String idusuarioS = params[0];
            String latitudS = params[1];
            String longitudS = params[2];

            Log.i(TAG, "prepara: idusr: " + idusuarioS +", lat:" + latitudS + ", lng: " + longitudS);

            HttpClient httpclientPanico = new DefaultHttpClient();
            HttpPost httppostPanico = new HttpPost(Config.BOTON_PANICO_UNIPOL_URL);

            try {
                AndroidMultiPartEntity entityPanico = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                entityPanico.addPart("idusr", new StringBody(idusuarioS));
                entityPanico.addPart("lat", new StringBody(String.valueOf(latitudS)));
                entityPanico.addPart("long", new StringBody(String.valueOf(longitudS)));

                totalSize = entityPanico.getContentLength();
                httppostPanico.setEntity(entityPanico);

                Log.i(TAG, "Total size: " + totalSize/1048576 + " MB");

                HttpResponse response = httpclientPanico.execute(httppostPanico);
                HttpEntity r_entity = response.getEntity();
                Log.i(TAG, "respuesta: " + response.toString());

                int statusCode = response.getStatusLine().getStatusCode();
                Log.i(TAG, "statusCode: " + statusCode);
                if (statusCode == 200) {
                    // Server response
                    responseStringPanico = EntityUtils.toString(r_entity);
                } else {
                    responseStringPanico = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            }catch (ClientProtocolException e) {
                responseStringPanico = e.toString();
            }catch (IOException e) {
                responseStringPanico = e.toString();
            }
            Log.i(TAG, "responseString: " + responseStringPanico);
            return responseStringPanico;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "Response from server: " + result);
            // showing the server response in an alert dialog

            try {
                JSONArray jsonRespuesta = new JSONArray(result);
                String respuesta = jsonRespuesta.getJSONObject(0).getString("respuesta");
                String mensaje = jsonRespuesta.getJSONObject(0).getString("mensaje");

                Log.i(TAG, "respuesta: " + respuesta);
                if(respuesta.equals("OK")){
                    progressDialog.dismiss();
                    showAlert("Su emergencia está siendo procesada");
                }
                else{
                    progressDialog.dismiss();
                    showAlert("Error! Tu denuncia No ha sido recibida. Intenta de nuevo.");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error envio: " + e.getMessage());
                progressDialog.dismiss();
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error en el envio.", Toast.LENGTH_LONG).show();
                finish();

            }

            super.onPostExecute(result);
        }
    }

    private void sendBotonPanico(String params) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String idusuario = jsonObject.getString("idusr");
            String lat = jsonObject.getString("lat");
            String lng = jsonObject.getString("long");

            jsonBody.put("idusr", idusuario);
            jsonBody.put("lat", lat);
            jsonBody.put("long", lng);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.BOTON_PANICO_UNIPOL_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String respuesta = "", mensaje = "";
                    try {
                        JSONArray array = new JSONArray(response);
                        JSONObject obj;
                        for (int i = 0; i < array.length(); i++) {
                            obj = array.getJSONObject(i);
                            respuesta = obj.getString("respuesta");
                            mensaje = obj.getString("mensaje");
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Log.i(TAG, "respuesta: " + respuesta);
                    if(respuesta.equals("OK")){
                        //showAlert(result);
                        //Toast.makeText(getApplicationContext(), "Publicación registrada correctamente", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        showAlert("Su emergencia está siendo procesada");
                        //finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error en la comunicación. Intente de nuevo.", Toast.LENGTH_LONG).show();
                        finish();
                    }

                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "VOLLEY: " + error.toString());
                    progressDialog.dismiss();
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

    // Location
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("Estos permisos son necesarios para enviar el reporte. Por favor habilítalos.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("El GPS no está habilitado");
        alertDialog.setMessage("¿Desea habilitar el GPS?");
        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(BotonPanicoActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        latitud = String.valueOf(loc.getLatitude());
        longitud = String.valueOf(loc.getLongitude());
        Log.i(TAG, "lat: " + loc.getLatitude() + ", long: " + loc.getLongitude());
        /*tvLatitude.setText(Double.toString(loc.getLatitude()));
        tvLongitude.setText(Double.toString(loc.getLongitude()));
        tvTime.setText(DateFormat.getTimeInstance().format(loc.getTime()));*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void showAlert(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Botón de Pánico")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // ok
                        finish();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();

    }



    @Override
    public void onBackPressed() { }
}
