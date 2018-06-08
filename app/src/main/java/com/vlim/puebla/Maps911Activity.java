package com.vlim.puebla;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps911Activity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    LatLng myPosition;
    FloatingActionButton imageButton;
    ConstraintLayout menu_map, mensaje_map;
    boolean isUp;
    View myView;
    double latitude, longitude;
    LocationManager locationManager;
    Integer MY_LOCATION_REQUEST_CODE = 23;
    Location location;
    Double latEnvia = 0.0, lngEnvia = 0.0;
    // Toolbar
    TextView tv_titulo_toolbar, tv_emergencia_a_reportar, tv_medica, tv_bomberos, tv_policia, tv_mensaje_click_mapa;
    ImageView btn_back;
    String idusuario;
    String TAG = "PUEBLA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps911);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(locationProviders == null || locationProviders.equals("")){
            showGPSDisabledAlertToUser();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent i= getIntent();
        idusuario = i.getStringExtra("idusuario");

        isUp = false;

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");
        // Toolbar
        tv_titulo_toolbar = (TextView) findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = (ImageView) findViewById(R.id.btn_back);

        tv_mensaje_click_mapa = findViewById(R.id.tv_mensaje_click_mapa);
        tv_mensaje_click_mapa.setTypeface(tf);
        tv_emergencia_a_reportar = findViewById(R.id.tv_emergencia_a_reportar);
        tv_emergencia_a_reportar.setTypeface(tf);
        tv_medica = findViewById(R.id.tv_medica);
        tv_medica.setTypeface(tf);
        tv_bomberos = findViewById(R.id.tv_bomberos);
        tv_bomberos.setTypeface(tf);
        tv_policia = findViewById(R.id.tv_policia);
        tv_policia.setTypeface(tf);

        menu_map = findViewById(R.id.menu_map);
        mensaje_map = findViewById(R.id.mensaje_map);

        /*imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*menu_map.setVisibility(View.VISIBLE);
                menu_map.setAlpha(0.0f);

                // Start the animation
                menu_map.animate()
                        .translationY(menu_map.getHeight())
                        .alpha(1.0f)
                        .setListener(null);*/

                /*if (isUp) {
                    slideDown(menu_map);
                } else {
                    slideUp(menu_map);
                }
                isUp = !isUp;
            }
        });*/

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    // slide the view from below itself to the current position
    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);

        setUpMap();


        // Add a marker and move the camera
        /*LatLng MEXICO = new LatLng(19.434545, -99.123456);
        Integer zoom = 15;
        mMap.addMarker(new MarkerOptions().position(MEXICO).title("Mi ubicación"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(mexico));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MEXICO, 15));*/

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
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            myPosition = new LatLng(latitude, longitude);

            LatLng coordinate = new LatLng(latitude, longitude);
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 19);
            mMap.animateCamera(yourLocation);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (isUp) {
                    slideDown(menu_map);
                    //mensaje_map.setVisibility(View.VISIBLE);
                    slideUp(mensaje_map);
                } else {
                    slideUp(menu_map);
                    //mensaje_map.setVisibility(View.INVISIBLE);
                    slideDown(mensaje_map);
                }
                isUp = !isUp;

            }
        });
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
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        /*Toast.makeText(this, "Info window clicked",
                Toast.LENGTH_SHORT).show();*/
        Intent reporteBotonDeLaMujer = new Intent(Maps911Activity.this, Reporte911Activity.class);
        reporteBotonDeLaMujer.putExtra("idusuario", idusuario);
        reporteBotonDeLaMujer.putExtra("latitud", String.valueOf(latEnvia));
        reporteBotonDeLaMujer.putExtra("longitud", String.valueOf(lngEnvia));
        startActivity(reporteBotonDeLaMujer);
        finish();
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
}
