package com.vlim.puebla;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class Sigueme2Activity extends FragmentActivity implements OnMapReadyCallback, LocationListener, AdapterView.OnItemClickListener,
        GoogleApiClient.ConnectionCallbacks{

    private GoogleMap mMap;
    PlaceAutocompleteFragment placeAutoComplete;
    static String TAG = "PUEBLA";
    Integer MY_LOCATION_REQUEST_CODE = 23;
    LocationManager locationManager;
    LatLng myPosition;
    Location location;
    double end_latitude = 0, end_longitude = 0;
    ConstraintLayout menu_map, mensaje_map, menu_cancela;
    Double latEnvia = 0.0, lngEnvia = 0.0;
    // Toolbar
    TextView tv_titulo_toolbar, tv_emergencia_a_reportar, tv_medica, tv_bomberos, tv_policia, tv_mensaje_click_mapa, tv_pregunta, tv_cancelar, tv_comenzar, tv_apie, tv_encarro, tv_cancelar_viaje;
    ImageView btn_back, img_apie, img_encarro;
    String idusuario;
    String opcionRuta = "DRIVING";  // 1: WALKING,   2: DRIVING

    // Rutas
    Object dataTransfer[] = new Object[2];
    Object dataTransferB[] = new Object[2];
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    ProgressDialog progressDialog;
    JSONArray jsonArr;
    String JsonResponse = null;
    String colorRuta = "naranja";

    // valida ubicacion en puebla
    EstaEnPuebla estaEnPuebla;
    Context context;
///////
    LocationCallback mLocationCallback;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    FusedLocationProviderClient mFusedLocationProviderClient;
    String idviaje = "0", nombreUsr;
    String lastLatitude, lastLongitude;

    private Handler mHandler;
    private Runnable mRunnable;

    Timer timerAsync;
    TimerTask timerTaskAsync;
    int times = 0;
    String lat1B, lat2B, lng1B, lng2B, idviajeB;
    int abduccion = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_siguemey_cuidame);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");
        // Toolbar
        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = findViewById(R.id.btn_back);
        tv_pregunta = findViewById(R.id.tv_pregunta);
        tv_cancelar = findViewById(R.id.tv_cancelar);
        tv_cancelar_viaje = findViewById(R.id.tv_cancelar_viaje);
        tv_comenzar = findViewById(R.id.tv_comenzar);
        tv_apie = findViewById(R.id.tv_apie);
        tv_encarro = findViewById(R.id.tv_encarro);
        img_apie = findViewById(R.id.img_apie);
        img_encarro = findViewById(R.id.img_encarro);
        menu_map = findViewById(R.id.menu_map);
        menu_cancela = findViewById(R.id.menu_cancela);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        tv_pregunta.setTypeface(tf);
        tv_cancelar.setTypeface(tf);
        tv_cancelar_viaje.setTypeface(tf);
        tv_comenzar.setTypeface(tf);
        tv_apie.setTypeface(tf);
        tv_encarro.setTypeface(tf);

        muestraMensaje();
        ////enviaSMSContactos();
        //repiteEnvioUbicacion();

        // Initialize a new instance of Handler
        mHandler = new Handler();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(locationProviders == null || locationProviders.equals("")){
            showGPSDisabledAlertToUser();
        }

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                    Log.d(TAG, "lat ahorita: " + lastLatitude + ", lng: " + lastLongitude);
                    // TODO: Get info about the selected place.
                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getAddress() + ", " + place.getLatLng());
                    end_latitude = place.getLatLng().latitude;
                    end_longitude = place.getLatLng().longitude;

                    // Obtener ruta
                    String url = getDirectionsUrl(opcionRuta);
                    Log.d(TAG, "URL: " + url);
                    dataTransfer = new Object[9];
                    url = getDirectionsUrl(opcionRuta);
                    GetDirectionsData getDirectionsData = new GetDirectionsData();
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = new LatLng(end_latitude, end_longitude);
                    //dataTransfer[3] = new LatLng(latitude, longitude);
                    dataTransfer[3] = Double.valueOf(lastLatitude);
                    dataTransfer[4] = Double.valueOf(lastLongitude);
                    dataTransfer[5] = end_latitude;
                    dataTransfer[6] = end_longitude;
                    dataTransfer[7] = getApplicationContext();
                    dataTransfer[8] = colorRuta;

                    getDirectionsData.execute(dataTransfer);
                    menu_map.setVisibility(View.VISIBLE);

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // lee datos del usuario
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        //Cursor c = db.query("Usuarios", campos, "idusuario=?", args, null, null, null);
        Cursor c = db.rawQuery("SELECT idusuario FROM Usuarios", null);

        if (c.moveToFirst()) {
            Log.v(TAG, "hay cosas inicio sigueme");
            //Recorremos el cursor hasta que no haya más registros
            do {
                idusuario = c.getString(0);
            } while(c.moveToNext());
            c.close();
        }
        else{
            Log.v(TAG, "NO hay cosas inicio");
        }
        db.close();
        //////

        buscaRutaActual();

        img_apie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcionRuta(1);
                actualizaRuta(opcionRuta, "naranja");
            }
        });

        img_encarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcionRuta(2);
                actualizaRuta(opcionRuta, "azul");
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_cancelar_viaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        Sigueme2Activity.this);
                builder.setTitle(Html.fromHtml("<font color='#ea7e01'>Sígueme y Cuídame</font>"));
                builder.setMessage(Html.fromHtml("<font color='black'>¿Deseas cancelar tu viaje?</font>"));
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // Toast.makeText(getApplicationContext(),"No is clicked",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                Log.d(TAG, "idViaje: " + idviaje);
                                cancelarViaje();
                                //showAlertCancelado("Viaje cancelado");
                            }
                        });
                builder.show();
            }
        });

        tv_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borraRuta();
                finish();
            }
        });

        tv_comenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia viaje

                JSONArray segmentoArray = new JSONArray();

                // Consulta tabla RutaSigueme
                //  ---------------- BD ------------------------------------------------------------------
                userSQLiteHelper usdbh =
                        new userSQLiteHelper(getApplicationContext(), "DBUsuarios", null, Config.VERSION_DB);
                SQLiteDatabase db = usdbh.getReadableDatabase();

                Cursor cRoute = db.rawQuery("SELECT * FROM RutaSigueme", null);
                if (cRoute.moveToFirst()) {
                    Log.d(TAG, "Borrando ruta previa. Genera nueva ruta");
                    //getDirectionsData.execute(dataTransfer);

                    while (!cRoute.isAfterLast()) {
                        String lat1 = cRoute.getString(cRoute.getColumnIndex("lat1"));
                        String lng1 = cRoute.getString(cRoute.getColumnIndex("lng1"));
                        String lat2 = cRoute.getString(cRoute.getColumnIndex("lat2"));
                        String lng2 = cRoute.getString(cRoute.getColumnIndex("lng2"));
                        JSONObject segmentos = new JSONObject();

                        //Log.d(TAG, "Cosas: " + lat1 + ", " + lng1 + ", " + lat2 + ", " + lng2);
                        try {
                            segmentos.put("lat", lat1);
                            segmentos.put("lng", lng1);
                            segmentos.put("lat", lat2);
                            segmentos.put("lng", lng2);
                            segmentoArray.put(segmentos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        cRoute.moveToNext();
                    }
                    cRoute.close();


                    JSONObject ruta = new JSONObject();
                    try {
                        ruta.put("ruta", segmentoArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject cosas = new JSONObject();
                    try {
                        cosas.put("latusr", String.valueOf(lastLatitude));
                        cosas.put("lonusr", String.valueOf(lastLongitude));
                        cosas.put("latdest", String.valueOf(end_latitude));
                        cosas.put("londest", String.valueOf(end_longitude));
                        cosas.put("idusr", String.valueOf(idusuario));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONArray siguemeJSON = new JSONArray();
                    siguemeJSON.put(cosas);
                    siguemeJSON.put(ruta);   // enviar este arreglo al servicio de ruta    // {"latusr":"valor","lonusr":"valor","latdest":"valor","londest":"valor","idusr":"valor","ruta":[{"lng":"valor","lat":"valor"},{"lng":"valor",º"lat":"valor"}]}
                    Log.d(TAG, "ruta: " + siguemeJSON.toString());

                    nuevaRuta(cosas, ruta);
                }
                else{
                    Log.d(TAG, "Genera nueva ruta");
                    //getDirectionsData.execute(dataTransfer);
                }

                // ----------------------------------------------------------------------------------------
            }
        });

    }

    private void enviaMensajeSMStest(String celular, String mensaje) {
        Log.d(TAG, "SMS TEST!");

        PackageManager pm = this.getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA)) {
            Toast.makeText(this, "Lo sentimos, tu dispositivo probablemente no pueda enviar SMS...", Toast.LENGTH_SHORT).show();
        }
        else{
            try{
                SmsManager sms = SmsManager.getDefault();
                //sms.sendTextMessage("+525555056736", null, "Hola!, mensaje test nalgas", null, null);
                sms.sendTextMessage(celular, null, mensaje, null, null);
                Log.d(TAG, "Enviando sms");
            }
            catch (Exception e) {
                Log.e(TAG, "SMS faild, please try again. " + e.getMessage());
                e.printStackTrace();
            }
        }

        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


    }

    private void cancelarViaje() {
        progressDialog = new ProgressDialog(Sigueme2Activity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cancelando viaje...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            jsonBody.put("idviaje", idviaje);

            final String requestBody = jsonBody.toString();

            Log.d(TAG, "envio Cancelar viaje: " + requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CANCELA_VIAJE_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "VOLLEY response CANCELA VIAJE: " + response);

                    String respuesta = null, mensaje = null;
                    try {
                        respuesta = jsonArr.getJSONObject(0).getString("respuesta");
                        mensaje = jsonArr.getJSONObject(0).getString("mensaje");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "respuesta: " + respuesta);
                    if(respuesta.equals("OK")){

                        Log.d(TAG, "CANCELANDO VIAJE");
                        /*borraRuta();
                        stopService(new Intent(getApplicationContext(), TrackingService.class));
                        startActivity(getIntent());*/
                        //finish();
                        //showAlertOK(mensaje);
                        showAlertCancelado("Viaje cancelado");
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error! Tu viaje no ha sido cancelado, por favor intenta de nuevo.", Toast.LENGTH_LONG).show();
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

    private void buscaRutaActual() {

        // busca ruta en proceso
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM RutaSigueme", null);

        if (c.moveToFirst()) {
            Log.v(TAG, "hay ruta en proceso");
            do {
                lat1B = c.getString(1);
                lng1B = c.getString(2);
                lat2B = c.getString(3);
                lng2B = c.getString(4);
                idviaje = c.getString(5);

                Log.d(TAG, "idViaje en progreso: " + idviaje + ", lat1B: " + lat1B + ", lngB: " + lng1B);

            } while(c.moveToNext());
            //Log.d(TAG, "idViaje en progreso: " + idviaje + ", lat1B: " + lat1B + ", lngB: " + lng1B);
            c.close();

            // Pintar ruta
            /*String url = getDirectionsUrl(opcionRuta);
            Log.d(TAG, "data transferB lat1: " + lat1B + ", " + lng1B + ", " + lat2B + ", " + lng2B);
            dataTransferB = new Object[9];
            url = getDirectionsUrl(opcionRuta);
            GetDirectionsData getDirectionsData = new GetDirectionsData();
            dataTransferB[0] = mMap;
            dataTransferB[1] = url;
            dataTransferB[2] = new LatLng(Double.valueOf(lat2B), Double.valueOf(lng2B));
            dataTransferB[3] = Double.valueOf(lat1B);
            dataTransferB[4] = Double.valueOf(lng1B);
            dataTransferB[5] = Double.valueOf(lat2B);
            dataTransferB[6] = Double.valueOf(lng2B);
            dataTransferB[7] = getApplicationContext();
            dataTransferB[8] = colorRuta;
            getDirectionsData.execute(dataTransferB);*/

            if(idviaje != null){
                menu_cancela.setVisibility(View.VISIBLE);
            }

            /*img_apie.setVisibility(View.INVISIBLE);
            img_encarro.setVisibility(View.INVISIBLE);
            tv_apie.setVisibility(View.INVISIBLE);
            tv_encarro.setVisibility(View.INVISIBLE);
            tv_comenzar.setVisibility(View.INVISIBLE);
            tv_pregunta.setText("Ruta en proceso");*/
        }
        else{
            Log.v(TAG, "NO hay ruta en proceso");
        }
        db.close();

    }

    private void nuevaRuta(JSONObject cosasObject, JSONObject rutaObject) {
        progressDialog = new ProgressDialog(Sigueme2Activity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Generando nueva ruta...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            String idusr = cosasObject.getString("idusr");
            String latusr = cosasObject.getString("latusr");
            String lonusr = cosasObject.getString("lonusr");
            String latdest = cosasObject.getString("latdest");
            String londest = cosasObject.getString("londest");
            String ruta = rutaObject.getString("ruta");

            jsonBody.put("idusr", idusr);
            jsonBody.put("latusr", latusr);
            jsonBody.put("lonusr", lonusr);
            jsonBody.put("latdest", latdest);
            jsonBody.put("londest", londest);
            jsonBody.put("ruta", ruta);

            String preRequestBody = jsonBody.toString();
            preRequestBody = preRequestBody.replace("\\", "");
            preRequestBody = preRequestBody.replace("\"ruta\":\"", "\"ruta\":");
            preRequestBody = preRequestBody.substring(0, preRequestBody.length() -2) + "}";

            final String requestBody = preRequestBody;

            Log.d(TAG, "envio: " + requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.NUEVA_RUTA_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.i("VOLLEYresponse", String.valueOf(jsonArr));
                    Log.i(TAG, "VOLLEY response nueva ruta: " + response);

                    String respuesta = null, mensaje = null;
                    try {
                        respuesta = jsonArr.getJSONObject(0).getString("respuesta");
                        mensaje = jsonArr.getJSONObject(0).getString("mensaje");
                        idviaje = mensaje;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "respuesta: " + respuesta + ", idViaje: " + idviaje);
                    if(respuesta.equals("OK")){

                        // inicia servicio, checa cada 5 minutos la posicion actual
                        Log.d(TAG, "Iniciando Servicio");

                        createLocationRequest();

                        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Log.d(TAG, "start foreground service");
                            getApplicationContext().startForegroundService(new Intent(getApplicationContext(), TrackingService.class));
                        } else {
                            Log.d(TAG, "start service");
                            startService(new Intent(getApplicationContext(), TrackingService.class));
                        }*/
                        
                        ////startService(new Intent(getApplicationContext(), TrackingService.class));
                        // actualiza numero de ruta en tabla sigueme

                        // Abrimos la bd
                        userSQLiteHelper rutaConn =
                                new userSQLiteHelper(getApplicationContext(), "DBUsuarios", null, Config.VERSION_DB);
                        SQLiteDatabase dbRuta = rutaConn.getReadableDatabase();
                        //Si hemos abierto correctamente la base de datos
                        if (dbRuta != null) {
                            dbRuta.execSQL("UPDATE RutaSigueme SET idviaje = '" + mensaje +"'");
                            dbRuta.close();
                        } else {
                            Log.v(TAG, "No Hay base");
                        }
                        showAlertOK("Se ha comenzado la ruta.");   // mensaje es el numero de ruta
                        //finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error! Tu ruta no ha sido generada correctamente, por favor intenta de nuevo.", Toast.LENGTH_LONG).show();
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

        private void showAlert(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Sígueme y cuídame")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // ok
                        //finish();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    private void showAlertOK(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Sígueme y cuídame")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // ok
                        finish();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    private void showAlertCancelado(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Sígueme y cuídame")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // ok
                        borraRuta();
                        stopService(new Intent(getApplicationContext(), TrackingService.class));
                        startActivity(getIntent());
                        finish();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();

    }


    private void actualizaRuta(String opcionRuta, String colorRuta) {
        // borra ruta actual
        borraRuta();
        // Obtener ruta
        String url = getDirectionsUrl(opcionRuta);
        Log.d(TAG, "URL: " + url);
        dataTransfer = new Object[9];
        url = getDirectionsUrl(opcionRuta);
        GetDirectionsData getDirectionsData = new GetDirectionsData();
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = new LatLng(end_latitude, end_longitude);
        dataTransfer[3] = Double.valueOf(lastLatitude);
        dataTransfer[4] = Double.valueOf(lastLongitude);
        dataTransfer[5] = end_latitude;
        dataTransfer[6] = end_longitude;
        dataTransfer[7] = getApplicationContext();
        dataTransfer[8] = colorRuta;

        getDirectionsData.execute(dataTransfer);
    }

    public void borraRuta() {
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        db.delete("RutaSigueme", null, null);
        db.close();
        Log.i(TAG, "Borrando bd RutaSigueme");
        mMap.clear();
    }

    private void opcionRuta(int opcion) {
        switch (opcion){
            case 1:
                opcionRuta = "WALKING";
                img_apie.setBackgroundResource(R.drawable.persona_caminando_azul);
                img_encarro.setBackgroundResource(R.drawable.carro);
                tv_apie.setTextColor(Color.parseColor("#0262e5"));
                tv_encarro.setTextColor(getResources().getColor(R.color.textogris));
                break;
            case 2:
                opcionRuta = "DRIVING";
                img_apie.setBackgroundResource(R.drawable.persona_caminando);
                img_encarro.setBackgroundResource(R.drawable.carro_azul);
                tv_encarro.setTextColor(Color.parseColor("#0262e5"));
                tv_apie.setTextColor(getResources().getColor(R.color.textogris));
                break;
                default:
                    break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setOnInfoWindowClickListener(this);

        setUpMap();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        final Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            /*latitude = location.getLatitude();
            longitude = location.getLongitude();*/
            lastLatitude = String.valueOf(location.getLatitude());
            lastLongitude = String.valueOf(location.getLongitude());

            Log.d(TAG, "1er vez mapa: " + lastLatitude + ", " + lastLongitude);
            //LatLng latLng = new LatLng(latitude, longitude);
            myPosition = new LatLng(Double.valueOf(lastLatitude), Double.valueOf(lastLongitude));

            LatLng coordinate = new LatLng(Double.valueOf(lastLatitude), Double.valueOf(lastLongitude));
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 19);
            mMap.animateCamera(yourLocation);

            estaEnPuebla = new EstaEnPuebla(getApplicationContext());
            ///////
            if(estaEnPuebla.estaEnPuebla(Double.valueOf(lastLatitude), Double.valueOf(lastLongitude))){
                Log.d(TAG, "Está en Puebla");
            }
            else{
                showAlertOK(Config.ESTA_EN_PUEBLA);
            }
        }
    }

    private void setUpMap() {
        if (mMap != null) {
            Log.d(TAG, "Mapa: " + mMap);

            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            Toast.makeText(this, "Error en permiso mapa", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Error en permisos ubicacion");

            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    MY_LOCATION_REQUEST_CODE);
        }

        if (location != null) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();
            myPosition = new LatLng(latitude, longitude);
            Log.d(TAG, "Location OK");

            LatLng coordinate = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 19);
            mMap.animateCamera(yourLocation);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChanged, provider: " + provider + ", status: " + status);

        // preguntar si está dentro del buffer
        bufferRuta();
        yaLlego();
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        /*Double latitude = loc.getLatitude();
        Double longitude = loc.getLongitude();*/
        lastLatitude = String.valueOf(loc.getLatitude());
        lastLongitude = String.valueOf(loc.getLongitude());
        Log.i(TAG, "lat: " + loc.getLatitude() + ", long: " + loc.getLongitude());


        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(location.getLatitude(), location.getLongitude()))
                .snippet("Emergencia")
                .title("Reporte");
        mMap.clear();
        mMap.addMarker(marker);

        bufferRuta();
        yaLlego();
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("El GPS de tu dispositivo está apagado. ¿Desea habilitarlo?")
                .setCancelable(false)
                .setPositiveButton("Activar GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                                finish();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        if(idviaje == null){
            borraRuta();
        }
        else{
            Log.d(TAG, "Hay ruta en proceso, no borrar!");
        }
    }

    // Rutas
    private String getDirectionsUrl(String opcionRuta)
    {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + lastLatitude + "," + lastLongitude);
        googleDirectionsUrl.append("&destination=" + end_latitude + "," + end_longitude);
        googleDirectionsUrl.append("&travelMode=" + opcionRuta);   // WALKING / DRIVING
        googleDirectionsUrl.append("&key=" + getApplicationContext().getString(R.string.google_maps_key_sigueme));
        return googleDirectionsUrl.toString();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void muestraMensaje() {
        new AlertDialog.Builder(Sigueme2Activity.this)
                .setTitle("Sígueme y Cuídame")
                .setMessage("Esta funcionalidad está sujeta a datos y batería del teléfono.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }


    ////////////////////////
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Config.INTERVALO_ACTUALIZACION_BUFFER);     //1 min
        mLocationRequest.setFastestInterval(Config.INTERVALO_MIN_ACTUALIZACION_BUFFER);
        mLocationRequest.setSmallestDisplacement(5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                /////bService.setVisibility(View.GONE);
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(Sigueme2Activity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    private void startLocationUpdates() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    //////updateMarker(location);
                    Log.d(TAG, "update location marker");
                    lastLatitude = String.valueOf(location.getLatitude());
                    lastLongitude = String.valueOf(location.getLongitude());
                    Log.d(TAG, "lat: " + lastLatitude + ", lng: " + lastLongitude);
                    bufferRuta();
                    yaLlego();
                }
            }

            ;
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(),"location permission required !!",Toast.LENGTH_SHORT).show();
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);
        /*button.setTag("f");
        button.setText("STOP FOREGROUND TRACKING");*/
//        Toast.makeText(getApplicationContext(),"Location update started",Toast.LENGTH_SHORT).show();
    }

    private void bufferRuta() {

        if(abduccion == 1){
            Log.d(TAG, "Abducido, no revisa buffer");

        }
        else{
            Log.d(TAG, "Revisa ruta");
            // lee idviaje
            Log.d(TAG, "Buffer Ruta");
            userSQLiteHelper usdbh =
                    new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
            SQLiteDatabase db = usdbh.getReadableDatabase();
            //Cursor c = db.query("Usuarios", campos, "idusuario=?", args, null, null, null);
            Cursor c = db.rawQuery("SELECT idviaje FROM RutaSigueme", null);

            if (c.moveToFirst()) {
                Log.v(TAG, "hay cosas buffer");
                //do {
                idviaje = c.getString(0);
                //} while(c.moveToNext());
            }
            else{
                Log.v(TAG, "NO hay cosas buffer");
            }
            db.close();
            //////

            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                final JSONObject jsonBody = new JSONObject();

                jsonBody.put("idviaje", idviaje);
                jsonBody.put("lat", lastLatitude);
                jsonBody.put("lng", lastLongitude);
                Log.d(TAG, "params buffer: idviaje: " + idviaje + ", lat: " + lastLatitude + ", lng: " + lastLongitude);

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
                        Log.i(TAG, "VOLLEY response buffer: " + response);
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
                                abduccion = 1;
                                repiteEnvioUbicacion();
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

    }

    private void yaLlego() {
        // ya llego al destino?
        //xx.123456    diferencia a partir del cuarto decimal
        Log.d(TAG, "Checando si ya llegó al destino");
        buscaRutaActual();
        Log.d(TAG, "latF: " + lat2B + ", lngF: " + lng2B);

        Log.d(TAG, "Ya llegó?, actual: " + lastLatitude + ", " + lastLongitude);
        Double diff = Math.abs(Double.valueOf(lat2B)-Double.valueOf(lastLatitude));
        Log.d(TAG, "Diff: " + diff);
        if(diff > 0.00002){
            Log.d(TAG, "No ha llegado");
        }
        else{
            Log.d(TAG, "Ya llegó");
            // Terminar viaje
            terminaViaje();
        }

    }

    private void terminaViaje() {

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            jsonBody.put("idviaje", idviaje);

            final String requestBody = jsonBody.toString();

            Log.d(TAG, "envio Terminar viaje: " + requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.FIN_RUTA_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "VOLLEY response TERMINA VIAJE: " + response);

                    String respuesta = null, mensaje = null;
                    try {
                        respuesta = jsonArr.getJSONObject(0).getString("respuesta");
                        mensaje = jsonArr.getJSONObject(0).getString("mensaje");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "respuesta: " + respuesta);
                    if(respuesta.equals("OK")){

                        Log.d(TAG, "TERMINANDO VIAJE");
                        //showAlertCancelado("Viaje terminado");
                        Toast.makeText(getApplicationContext(),"Has llegado a tu destino. \nTerminando viaje.", Toast.LENGTH_LONG).show();

                        borraRuta();
                        stopLocationUpdates();
                        stopService(new Intent(getApplicationContext(), TrackingService.class));
                        vibrar();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Error! Tu viaje no ha sido terminado, por favor intenta de nuevo.", Toast.LENGTH_LONG).show();
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

    private void vibrar() {
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator.hasVibrator()) {
            final long[] pattern = {0, 500, 1000};
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

    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocation Updates");
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    private void abduccionEmergencia() {
        // lee idviaje
        Log.d(TAG, "Buffer Ruta");
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        //Cursor c = db.query("Usuarios", campos, "idusuario=?", args, null, null, null);
        Cursor c = db.rawQuery("SELECT idviaje FROM RutaSigueme", null);

        if (c.moveToFirst()) {
            Log.v(TAG, "hay cosas abduccion");
            //do {
            idviaje = c.getString(0);
            //} while(c.moveToNext());
        }
        else{
            Log.v(TAG, "NO hay cosas abduccion");
        }
        db.close();
        //////

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            jsonBody.put("idviaje", idviaje);
            jsonBody.put("lat", lastLatitude);
            jsonBody.put("lng", lastLongitude);
            Log.d(TAG, "params abduccion: idviaje: " + idviaje + ", lat: " + lastLatitude + ", lng: " + lastLongitude);

            final String requestBody = jsonBody.toString();

            Log.d(TAG, "params abduccion: " + requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ABDUCCION_EMERGENCIA_RUTA_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "VOLLEY response abduccion: " + response);
                    //Toast.makeText(getApplicationContext(), "Buffer: " + response, Toast.LENGTH_LONG).show();
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = jsonArr.getJSONObject(0);


                        /*if(jsonObj.getString("respuesta").equals("Error")){
                            Toast.makeText(getApplicationContext(), "Ha salido de la ruta, enviando posición.", Toast.LENGTH_LONG).show();
                            muestraNotificacion();
                            enviaSMSContactos();
                            borraRuta();
                            stopLocationUpdates();

                            abduccionEmergencia();
                            stopService(new Intent(getApplicationContext(), TrackingService.class));
                        }
                        else{
                            Log.d(TAG, "Sigue en ruta");
                        }*/
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

    private void repiteEnvioUbicacion() {
        Log.d(TAG, "repite envio ubicación");

        timerAsync = new Timer();
        timerTaskAsync = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        if(times < 30){
                            Log.i(TAG,"Enviando posición después de abducción...: " + times);
                            abduccionEmergencia();
                            times++;
                        }
                        else{
                            Log.d(TAG, "Termina repetición.");
                            timerAsync.cancel();
                            stopLocationUpdates();                        }
                    }
                });
            }
        };
        /// delay 1 seg, repite cada 3 segundos
        timerAsync.schedule(timerTaskAsync, 1000, 60000);

    }

    private void stopRepeatingTask() {
        mHandler.removeCallbacks(mRunnable);
    }

    private void startRepeatingTask() {
        mRunnable.run();
    }

    public void muestraNotificacion() {

        Log.d(TAG, "Enviando notificación");

        Context context = getApplicationContext();
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this);

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentTitle("Puebla");
        notificationBuilder.setContentText("Sígueme y Cuídame");

        Intent notificationClickIntent = new Intent(this, NotificationActionActivity.class);
        PendingIntent notificationPendingIntent =
                PendingIntent.getActivity(context, 0, notificationClickIntent, 0);

        notificationBuilder.setContentIntent(notificationPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(1, notificationBuilder.build());


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
                Log.v(TAG, "hay cosas envia SMS");
                //Recorremos el cursor hasta que no haya más registros
                do {
                    idusuario = c.getString(0);
                    nombreUsr = c.getString(2);
                } while(c.moveToNext());
            }
            else{
                Log.v(TAG, "NO hay cosas SMS");
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

                                    //Thread.sleep(1000);
                                    ///obtieneDetalleContacto(id_usuario_contacto);
                                    //enviaMensajeSMStest("+525555056736", "Hola!");


                                    // obtiene detalle contacto
                                    try {
                                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                        final JSONObject jsonBody = new JSONObject();

                                        jsonBody.put("idusr", id_usuario_contacto);
                                        final String requestBody = jsonBody.toString();

                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_INFO_CONTACTO_URL, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    jsonArr = new JSONArray(response);
                                                    JsonResponse = response;
                                                    final String[] nombre_completo = new String[1];
                                                    final String[] celular = new String[1];
                                                    Log.i(TAG, "response: " + jsonArr);

                                                    for (int i = 0; i < jsonArr.length(); i++) {



                                                                JSONObject jsonObj = null;
                                                                try {
                                                                    jsonObj = jsonArr.getJSONObject(i);

                                                                    nombre_completo[0] = jsonObj.getString("nombre_completo");
                                                                    //String telefono = jsonObj.getString("telefono");
                                                                    celular[0] = "+52" + jsonObj.getString("celular");
                                                                    //String correo_contacto = jsonObj.getString("correo_contacto");
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }


                                                                Log.i(TAG, "info contacto nombre: " + nombre_completo[0] + ", tel: " + celular[0]);
                                                                buscaRutaActual();
                                                                String id_ruta = idviaje;
                                                                Log.d(TAG, "id_ruta: " + idviaje);

                                                                String mensajeSMS = "Alerta: " + nombre_completo[0] + " se ha desviado de su ruta. Comuniquese al 01 800 624 2330 con el ID RUTA: "
                                                                        + id_ruta +" para mayor informacion";

                                                                Log.d(TAG, "sms: " + mensajeSMS);
                                                                String celSMS = celular[0];
                                                                String nombreCom = nombre_completo[0];

                                                                ////enviaMensajeSMStest(celSMS, mensajeSMS.trim());


                                                                PackageManager pm = getApplicationContext().getPackageManager();

                                                                if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                                                                        !pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA)) {
                                                                    Toast.makeText(getApplicationContext(), "Lo sentimos, tu dispositivo probablemente no pueda enviar SMS...", Toast.LENGTH_SHORT).show();
                                                                }
                                                                else{
                                                                    try{
                                                                        SmsManager sms = SmsManager.getDefault();
                                                                        //sms.sendTextMessage("+525555056736", null, "Hola!, mensaje test nalgas", null, null);
                                                                        sms.sendTextMessage(celSMS, null, mensajeSMS, null, null);
                                                                        Log.d(TAG, "Enviando sms");
                                                                    }
                                                                    catch (Exception e) {
                                                                        Log.e(TAG, "SMS faild, please try again. " + e.getMessage());
                                                                        e.printStackTrace();
                                                                    }
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




                                    ////////////////


                                    Log.d(TAG, "Espera...");

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

    /*private void obtieneDetalleContacto(String id_contacto) {

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
                        final String[] nombre_completo = new String[1];
                        final String[] celular = new String[1];
                        Log.i(TAG, "response: " + jsonArr);

                        for (int i = 0; i < jsonArr.length(); i++) {


                            final int finalI = i;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject jsonObj = null;
                                    try {
                                        jsonObj = jsonArr.getJSONObject(finalI);

                                        nombre_completo[0] = jsonObj.getString("nombre_completo");
                                        //String telefono = jsonObj.getString("telefono");
                                        celular[0] = "+52" + jsonObj.getString("celular");
                                        //String correo_contacto = jsonObj.getString("correo_contacto");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                    Log.i(TAG, "info contacto nombre: " + nombre_completo[0] + ", tel: " + celular[0]);

                                    String mensajeSMS = "Alerta: " + nombreUsr + " ha salido de su ruta. Su última ubicación es: https://www.google.com/maps/search/?api=1&query=" + lastLatitude + "," + lastLongitude;
                                    Log.d(TAG, "sms: " + mensajeSMS);

                                    enviaMensajeSMStest(celular[0], mensajeSMS);
                                }
                            }, 2000);

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
    }*/

}
