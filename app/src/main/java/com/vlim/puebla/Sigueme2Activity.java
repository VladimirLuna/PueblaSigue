package com.vlim.puebla;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class Sigueme2Activity extends FragmentActivity implements OnMapReadyCallback, LocationListener, AdapterView.OnItemClickListener,
        GoogleApiClient.ConnectionCallbacks{

    private GoogleMap mMap;
    PlaceAutocompleteFragment placeAutoComplete;
    String TAG = "PUEBLA";
    Integer MY_LOCATION_REQUEST_CODE = 23;
    LocationManager locationManager;
    LatLng myPosition;
    Location location;
    double latitude, longitude, end_latitude = 0, end_longitude = 0;
    ConstraintLayout menu_map, mensaje_map;
    Double latEnvia = 0.0, lngEnvia = 0.0;
    // Toolbar
    TextView tv_titulo_toolbar, tv_emergencia_a_reportar, tv_medica, tv_bomberos, tv_policia, tv_mensaje_click_mapa, tv_pregunta, tv_cancelar, tv_comenzar, tv_apie, tv_encarro;
    ImageView btn_back, img_apie, img_encarro;
    String idusuario;
    String opcionRuta = "DRIVING";  // 1: WALKING,   2: DRIVING

    // Rutas
    Object dataTransfer[] = new Object[2];
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;

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
        tv_comenzar = findViewById(R.id.tv_comenzar);
        tv_apie = findViewById(R.id.tv_apie);
        tv_encarro = findViewById(R.id.tv_encarro);
        img_apie = findViewById(R.id.img_apie);
        img_encarro = findViewById(R.id.img_encarro);
        menu_map = findViewById(R.id.menu_map);

        tv_pregunta.setTypeface(tf);
        tv_cancelar.setTypeface(tf);
        tv_comenzar.setTypeface(tf);
        tv_apie.setTypeface(tf);
        tv_encarro.setTypeface(tf);

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
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getAddress() + ", " + place.getLatLng());
                end_latitude = place.getLatLng().latitude;
                end_longitude = place.getLatLng().longitude;

                // Obtener ruta
                String url = getDirectionsUrl(opcionRuta);
                Log.d(TAG, "URL: " + url);
                dataTransfer = new Object[8];
                url = getDirectionsUrl(opcionRuta);
                GetDirectionsData getDirectionsData = new GetDirectionsData();
                dataTransfer[0] = mMap;
                dataTransfer[1] = url;
                dataTransfer[2] = new LatLng(end_latitude, end_longitude);
                //dataTransfer[3] = new LatLng(latitude, longitude);
                dataTransfer[3] = latitude;
                dataTransfer[4] = longitude;
                dataTransfer[5] = end_latitude;
                dataTransfer[6] = end_longitude;
                dataTransfer[7] = getApplicationContext();

                getDirectionsData.execute(dataTransfer);
                menu_map.setVisibility(View.VISIBLE);

                // borra anteriores rutas, si es que aplica
                //  ---------------- BD ------------------------------------------------------------------
                /*userSQLiteHelper usdbh =
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

                        Log.d(TAG, "Cosas: " + lat1);
                        cRoute.moveToNext();
                    }

                    // {"latusr":"valor","lonusr":"valor","latdest":"valor","londest":"valor","idusr":"valor","ruta":[{"lng":"valor","lat":"valor"},{"lng":"valor",º"lat":"valor"}]}
                }
                else{
                    Log.d(TAG, "Genera nueva ruta");
                    //getDirectionsData.execute(dataTransfer);
                }*/

                /*if (db != null) {
                    //Insertamos los datos en la tabla Usuarios
                    db.execSQL("INSERT INTO RutaSigueme (lat1, lng1, lat2, lng2) VALUES ('" + lat1 + "', '" + lng1 + "', '" + lat2 + "', '" + lng2 + "')");
                    Log.d(TAG, "Hay Ruta previa");
                    db.close();
                }
                else {
                    Log.v(TAG, "No Hay base en donde guardar!");
                }*/


                // ----------------------------------------------------------------------------------------

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

        img_apie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcionRuta(1);
                actualizaRuta(opcionRuta);
            }
        });

        img_encarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opcionRuta(2);
                actualizaRuta(opcionRuta);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

            }
        });

    }

    private void actualizaRuta(String opcionRuta) {
        // borra ruta actual
        borraRuta();
        // Obtener ruta
        String url = getDirectionsUrl(opcionRuta);
        Log.d(TAG, "URL: " + url);
        dataTransfer = new Object[8];
        url = getDirectionsUrl(opcionRuta);
        GetDirectionsData getDirectionsData = new GetDirectionsData();
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = new LatLng(end_latitude, end_longitude);
        dataTransfer[3] = latitude;
        dataTransfer[4] = longitude;
        dataTransfer[5] = end_latitude;
        dataTransfer[6] = end_longitude;
        dataTransfer[7] = getApplicationContext();

        getDirectionsData.execute(dataTransfer);
    }

    private void borraRuta() {
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
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG, "1er vez mapa: " + latitude + ", " + longitude);
            //LatLng latLng = new LatLng(latitude, longitude);
            myPosition = new LatLng(latitude, longitude);

            LatLng coordinate = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 19);
            mMap.animateCamera(yourLocation);
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
            latitude = location.getLatitude();
            longitude = location.getLongitude();
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

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();
        Log.i(TAG, "lat: " + loc.getLatitude() + ", long: " + loc.getLongitude());

        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude))
                .snippet("Emergencia")
                .title("Reporte");
        mMap.clear();
        mMap.addMarker(marker);
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
    public void onBackPressed() { }

    // Rutas
    private String getDirectionsUrl(String opcionRuta)
    {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + latitude + "," + longitude);
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
}
