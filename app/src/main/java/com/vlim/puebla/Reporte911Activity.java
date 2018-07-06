package com.vlim.puebla;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import java.util.ArrayList;

public class Reporte911Activity extends AppCompatActivity implements LocationListener {

    String TAG = "PUEBLA";
    String id_usuario = "", tipo_emergencia = "";
    Spinner spinner_submotivos;
    TextView tv_titulo_toolbar, tv_mensaje1, tv_titulo_reporte, tv_descripcion, tv_fotografia, tv_video, tv_audio, tv_videocapturado, tv_audiocapturado;
    EditText et_titulo_reporte, et_descripcion;
    Button btn_enviar;
    ImageView btn_back, btn_camara, btn_video, btn_audio, imageView, imgvideoPrev;
    String tipo_submotivo;
    Typeface tf;
    JSONArray jsonArr;
    String JsonResponse = null;
    ArrayList tipoIncidenciaList;
    NetworkConnection nt_check;
    String idTipoIncidencia[];
    public static final int REQUEST_CODE_CAMERA = 23;
    public static final int REQUEST_CODE_CAMERA_OLDIE = 24;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private Uri fileUri; // file url to store image/video
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_OLDIE = 1010;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int REQUEST_ACESS_STORAGE = 3;
    private static final int REQUEST_ACESS_CAMERA = 2;
    private static final int GPS_ACTIVATION_REQUEST = 66;
    public static final int SELECT_FILE = 3;
    Uri selectedImage;
    String photoPath = "", videoPath = "",  photoGaleryPath = "";
    Bitmap fotoBitmap, videoBitmap, photo, galeriaBitmap;
    VideoView videoPrev;
    String idusuario;
    String titulo_submotivo, descrp_submotivo;
    long totalSize = 0;
    String responseStringIncidencia = null;
    String responseStringIncidenciaArchivos = null;
    String latitud, longitud;
    private ProgressBar progressBar;
    private TextView txtPercentage;
    ProgressDialog progressDialogLista, progressDialogEnvio, progressDialogVideo, progressDialogVideoGal;

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 2000;  //1000*60*1
    LocationManager locationManager;
    Location loc;

    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();

    Uri tempUriCameraDeviceNotSuported;
    String photoPathDB = "", videoPathDB = "", photoGaleryPathDB = "";
    String fileVideoCompressedPath = "", fileVideoGalCompressedPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_reporte911);

        Intent i= getIntent();
        id_usuario = i.getStringExtra("idusuario");
        tipo_emergencia = i.getStringExtra("emergencia");

        Log.d(TAG, "usr: " + id_usuario + ", emergencia: " + tipo_emergencia);

        progressBar = findViewById(R.id.progressBarAnonima);
        progressBar.setVisibility(View.INVISIBLE);

        // obtiene posicion
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

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

            spinner_submotivos = findViewById(R.id.spinner);
            tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
            tv_mensaje1 = findViewById(R.id.tv_mensaje1);
            tv_titulo_reporte = findViewById(R.id.tv_nombre);
            tv_descripcion = findViewById(R.id.tv_afiliado);
            tv_fotografia = findViewById(R.id.tv_fotografia);
            tv_video = findViewById(R.id.tv_video);
            tv_audio = findViewById(R.id.tv_audio);
            et_titulo_reporte = findViewById(R.id.et_nombre);
            et_descripcion = findViewById(R.id.et_afiliado);
            btn_enviar = findViewById(R.id.btn_enviar);
            btn_back = findViewById(R.id.btn_back);
            btn_camara = findViewById(R.id.btn_pdf);
            btn_video = findViewById(R.id.btn_audio);
            btn_audio = findViewById(R.id.btn_audio);
            txtPercentage = (TextView) findViewById(R.id.txtPercentage);
            txtPercentage.setTypeface(tf);
            tv_videocapturado = (TextView) findViewById(R.id.tv_audiocapturado);
            tv_videocapturado.setVisibility(View.INVISIBLE);
            tv_videocapturado.setTypeface(tf);

            tv_titulo_toolbar.setTypeface(tf);
            tv_mensaje1.setTypeface(tf);
            tv_titulo_reporte.setTypeface(tf);
            tv_descripcion.setTypeface(tf);
            tv_fotografia.setTypeface(tf);
            tv_video.setTypeface(tf);
            tv_audio.setTypeface(tf);
            et_titulo_reporte.setTypeface(tf);
            et_descripcion.setTypeface(tf);
            btn_enviar.setTypeface(tf);

            imageView = (ImageView) findViewById(R.id.Imageprev);
            imageView.setVisibility(View.INVISIBLE);
            videoPrev = (VideoView) findViewById(R.id.videoPreview);
            videoPrev.setVisibility(View.INVISIBLE);
            imgvideoPrev = (ImageView) findViewById(R.id.imgvideoPrev);
            imgvideoPrev.setVisibility(View.INVISIBLE);
            tv_videocapturado = (TextView) findViewById(R.id.tv_audiocapturado);
            tv_videocapturado.setVisibility(View.INVISIBLE);
            tv_videocapturado.setTypeface(tf);
            tv_audiocapturado = (TextView) findViewById(R.id.tv_audiocapturado);
            tv_audiocapturado.setVisibility(View.INVISIBLE);
            tv_audiocapturado.setTypeface(tf);

            switch(tipo_emergencia){
                case "medica":
                    obtieneMotivos(2);
                    break;
                case "policia":
                    obtieneMotivos(1);
                    break;
                case "bomberos":
                    obtieneMotivos(5);
                    break;
                    default:
                        break;
            }

            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            } else {
                //readDataExternal();
            }

            Log.i(TAG, "version: " + android.os.Build.VERSION.SDK_INT + ", M: " + Build.VERSION_CODES.M);
        }

        tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("El GPS de tu dispositivo está apagado");
        alertDialog.setMessage("¿Desea habilitarlo?");
        alertDialog.setPositiveButton("Activar GPS", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_ACTIVATION_REQUEST);
            }
        });
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                borraMedios();
                Toast.makeText(getApplicationContext(), "Es necesario activar el GPS para enviar el reporte de Incidencias", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        alertDialog.show();
    }

    private void borraMedios() {
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        db.delete("Media", null, null);
        db.close();
        Log.i(TAG, "Borrando bd medios");
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

    private void obtieneMotivos(int motivo) {

        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("institucion" , motivo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            Log.v(TAG, "postdic len: " + String.valueOf(post_dict));


            servicioObtieneSubMotivos(String.valueOf(post_dict));
        }


        /*RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final JSONObject jsonBody = new JSONObject();


        try {
            jsonBody.put("institucion", '1');
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_SUBMOTIVOS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jsonArr = new JSONArray(response);
                    JsonResponse = response;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Log.i("VOLLEYresponse", String.valueOf(jsonArr));
                Log.i(TAG, "VOLLEYresponse: " + response);
                // [{"id_motivo":16,"id_instituciones":0,"submotivo":"AMENAZA DE ABORTO","nombre_motivo_llamada":null,"prioridad":null,"activo":false}]


                tipoIncidenciaList = new ArrayList();
                idTipoIncidencia = new String[10];

                for (int i = 0; i < jsonArr.length(); i++){
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = jsonArr.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        String incidencia_desc = jsonObj.getString("incidencia_desc");
                        String id_tipo_incidencia = jsonObj.getString("id_tipo_incidencia");
                        System.out.println(id_tipo_incidencia + ", " + incidencia_desc);

                        idTipoIncidencia[i] = id_tipo_incidencia;
                        tipoIncidenciaList.add(incidencia_desc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }// for

                progressDialogLista.dismiss();

                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Reporte911Activity.this, android.R.layout.simple_spinner_dropdown_item, tipoIncidenciaList){
                    public View getView(int position, View convertView, android.view.ViewGroup parent) {
                        TextView v = (TextView) super.getView(position, convertView, parent);
                        v.setTypeface(tf);
                        v.setTextColor(Color.WHITE);
                        return v;
                    }
                    public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                        TextView v = (TextView) super.getView(position, convertView, parent);
                        v.setTypeface(tf);
                        return v;
                    }
                };
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_tipo_incidencia.setAdapter(adapter1);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
                progressDialogLista.dismiss();
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
        requestQueue.add(stringRequest);*/
    }

    private void servicioObtieneSubMotivos(String params) {

        progressDialogLista = new ProgressDialog(Reporte911Activity.this);
        progressDialogLista.setCancelable(false);
        progressDialogLista.setMessage("Obteniendo información...");
        progressDialogLista.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialogLista.setProgress(0);
        progressDialogLista.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String institucion = jsonObject.getString("institucion");
            jsonBody.put("institucion", institucion);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_SUBMOTIVOS_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.i("VOLLEYresponse", String.valueOf(jsonArr));
                    Log.i(TAG, "VOLLEY response submotivos: " + response);

                    tipoIncidenciaList = new ArrayList();
                    idTipoIncidencia = new String[10];

                    for (int i = 0; i < jsonArr.length(); i++){
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = jsonArr.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            String incidencia_desc = jsonObj.getString("submotivo");
                            String id_tipo_incidencia = jsonObj.getString("id_motivo");
                            System.out.println(id_tipo_incidencia + ", " + incidencia_desc);

                            idTipoIncidencia[i] = id_tipo_incidencia;
                            tipoIncidenciaList.add(incidencia_desc);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }// for

                    progressDialogLista.dismiss();

                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(Reporte911Activity.this, android.R.layout.simple_spinner_dropdown_item, tipoIncidenciaList){
                        public View getView(int position, View convertView, android.view.ViewGroup parent) {
                            TextView v = (TextView) super.getView(position, convertView, parent);
                            v.setTypeface(tf);
                            v.setTextColor(Color.BLACK);
                            return v;
                        }
                        public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                            TextView v = (TextView) super.getView(position, convertView, parent);
                            v.setTypeface(tf);
                            return v;
                        }
                    };
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner_submotivos.setAdapter(adapter1);

                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "VOLLEY: " + error.toString());
                    progressDialogLista.dismiss();
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
            Log.d(TAG, "provider: " + provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e(TAG, "error last location: " + e.toString());
        }
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
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "onStatusChange");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "onProviderEnabled");
        getLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "onProviderDisabled");
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onBackPressed() { }
}
