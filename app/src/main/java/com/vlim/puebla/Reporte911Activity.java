package com.vlim.puebla;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import com.iceteck.silicompressorr.SiliCompressor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.vlim.puebla.AjustesChatActivity.requestPermission;

public class Reporte911Activity extends AppCompatActivity implements LocationListener {

    String TAG = "PUEBLA";
    String id_usuario = "", tipo_emergencia = "";
    Spinner spinner_submotivos;
    TextView tv_titulo_toolbar, tv_mensaje1, tv_motivo, tv_descripcion;
    EditText et_descripcion;
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
    ProgressDialog progressDialog;

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

    TabLayout MyTabs;
    ViewPager MyPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_reporte911_2);

        tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        //declare views
        MyTabs = findViewById(R.id.MyTabs);
        MyPage = findViewById(R.id.MyPage);

        MyTabs.setupWithViewPager(MyPage);
        SetUpViewPager(MyPage);

        Intent i= getIntent();
        id_usuario = i.getStringExtra("idusuario");
        tipo_emergencia = i.getStringExtra("emergencia");

        Log.d(TAG, "usr: " + id_usuario + ", emergencia: " + tipo_emergencia);

        progressBar = findViewById(R.id.progressBar911);
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
            tv_motivo = findViewById(R.id.tv_motivo);
            tv_descripcion = findViewById(R.id.tv_descripcion);
            et_descripcion = findViewById(R.id.et_descripcion);
            btn_enviar = findViewById(R.id.btn_enviar);
            btn_back = findViewById(R.id.btn_back);
            btn_camara = findViewById(R.id.btn_foto);
            btn_video = findViewById(R.id.btn_video);
            btn_audio = findViewById(R.id.btn_audio);
            txtPercentage = findViewById(R.id.txtPercentage);
            txtPercentage.setTypeface(tf);

            tv_titulo_toolbar.setTypeface(tf);
            tv_mensaje1.setTypeface(tf);
            tv_motivo.setTypeface(tf);
            tv_descripcion.setTypeface(tf);
            et_descripcion.setTypeface(tf);
            btn_enviar.setTypeface(tf);

            /*imageView = findViewById(R.id.Imageprev);
            imageView.setVisibility(View.INVISIBLE);
            videoPrev = findViewById(R.id.videoPreview);
            videoPrev.setVisibility(View.INVISIBLE);
            imgvideoPrev = findViewById(R.id.imgvideoPrev);
            imgvideoPrev.setVisibility(View.INVISIBLE);*/

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

        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirCamara();
            }
        });

        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void abrirCamara() {
        String fileName = System.currentTimeMillis()+".jpg";

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
        else{
            Log.i(TAG, "version oldie");

            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)) {
                Log.i(TAG, "aquii....**");
                startDialog();
            }else{
                requestPermission(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_ACESS_STORAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Galeria fotos: " + requestCode + ", " + resultCode);

        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getWritableDatabase();

       /* Cursor c = db.rawQuery("SELECT photopath, videopath, galeriapath FROM Media WHERE idmedio == 1", null);
        if (c.moveToFirst()) {
            Log.i(TAG, "ya existen medios");
        }
        else{
            Log.v(TAG, "NO hay cosas, crear id 1");
            //Abrimos la base de datos 'DBUsuarios' en modo escritura
            if (db != null) {
                //Insertamos los datos en la tabla Usuarios
                db.execSQL("INSERT INTO Media (idmedio) VALUES (1)");
            } else {
                Log.v(TAG, "No Hay base");
            }
        }*/

        if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_OLDIE){

            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Imagen capturada oldie");
                //Log.e(TAG, "PATH oldie: " + photoPath);

                if (data.hasExtra("data")) {
                    Log.e(TAG, "Hay extras: ");
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                    fileUri = getImageUri(Reporte911Activity.this, bitmap);
                    File finalFile = new File(getRealPathFromUri(fileUri));
                    btn_camara.setVisibility(View.VISIBLE);  //imageView
                    //btn_camara.setVisibility(View.INVISIBLE);
                    //btn_camara.setImageBitmap(bitmap);
                    //Glide.with(this).load(bitmap).into(btn_camara);
                    Log.e(TAG, "finalFile: " + finalFile.getPath());

                    photoPath = getPath(fileUri);
                    System.out.println("Image Path : " + photoPath);

                    // Guarda en BD
                    //Si hemos abierto correctamente la base de datos
                    if (db != null) {
                        //Insertamos los datos en la tabla Usuarios
                        /*db.execSQL("UPDATE Media SET photopath = '" + photoPath +"' WHERE idmedio == 1");
                        Log.v(TAG, "UPDATE Media SET medio = '" + photoPath +" tipo = 'foto' WHERE idmedio == 1");*/

                        db.execSQL("INSERT INTO Media (medio, tipo) VALUES ('" + photoPath + "', foto)");
                        Log.v(TAG, "INSERT INTO Media (medio, tipo) VALUES ('" + photoPath +", foto ");
                    } else {
                        Log.v(TAG, "No Hay base");
                    }

                    ///////actualizaPrevios();

                } else if (data.getExtras() == null) {
                    Log.e(TAG, "nulos extras");
                    Toast.makeText(getApplicationContext(),
                            "No extras to retrieve!", Toast.LENGTH_SHORT)
                            .show();

                    /*BitmapDrawable thumbnail = new BitmapDrawable(
                            getResources(), data.getData().getPath());
                    imageView.setImageDrawable(thumbnail);*/

                }
                else{
                    Log.e(TAG, "no extras");
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Cámara cancelada",
                        Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE){
            try {
                photoPath = getPath(fileUri);
                System.out.println("Image Path : " + photoPath);
                fotoBitmap = decodeUri(fileUri);
                //imageView.setVisibility(View.VISIBLE);
                //galeriaPrev.setVisibility(View.VISIBLE);
                //btn_camara.setVisibility(View.INVISIBLE);
                //btn_camara.setImageBitmap(photo);
                ////btn_camara.setImageBitmap(fotoBitmap);
                //Picasso.with(getApplicationContext()).load(photoPath).into(btn_camara);
                Log.d(TAG, "Foto capturada: " + photoPath);

                // Guarda en BD
                //Si hemos abierto correctamente la base de datos
                if (db != null) {
                    //Insertamos los datos en la tabla Usuarios
                    //db.execSQL("UPDATE Media SET photopath = '" + photoPath +"' WHERE idmedio == 1");

                    db.execSQL("INSERT INTO Media (medio, tipo) VALUES ('" + photoPath + "', 'foto')");
                    Log.v(TAG, "INSERT INTO Media (medio, tipo) VALUES ('" + photoPath +"', 'foto' ");
                } else {
                    Log.v(TAG, "No Hay base");
                }
                //////actualizaPrevios();

            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {

            Log.d(TAG, "Video capturado: " + videoPath);
            //Log.d(TAG, "Hay foto: " + photoPath);
            //btn_video.setEnabled(false);
            //btn_video.setVisibility(View.INVISIBLE);
            /*imgvideoPrev.setVisibility(View.VISIBLE);
            tv_videocapturado.setVisibility(View.VISIBLE);*/

            Uri videoCaptured = data.getData();
            String[] videoPathColumn = { MediaStore.Images.Media.DATA };
            //Log.d(TAG, "Video grabado: " + videoPathColumn);

            Cursor cursorvid = getContentResolver().query(videoCaptured,
                    videoPathColumn, null, null, null);
            cursorvid.moveToFirst();

            int columnIndex = cursorvid.getColumnIndex(videoPathColumn[0]);
            videoPath = cursorvid.getString(columnIndex);
            Log.d(TAG, "Video para cambiar nombre: " + videoPath);
            cursorvid.close();

            //Si hemos abierto correctamente la base de datos
            if (db != null) {
                //db.execSQL("UPDATE Media SET videopath = '" + videoPath +"' WHERE idmedio == 1");

                db.execSQL("INSERT INTO Media (medio, tipo) VALUES ('" + videoPath + "', video)");
                Log.v(TAG, "INSERT INTO Media (medio, tipo) VALUES ('" + videoPath +", video ");
            } else {
                Log.v(TAG, "No Hay base");
            }

            //////actualizaPrevios();


            File f1 = new File(videoPath);
            VideoCompressAsyncTask videoCompressAsyncTask = new VideoCompressAsyncTask(getApplicationContext());
            videoCompressAsyncTask.execute(f1.getPath().toString(), f1.getPath().toString());
            Log.i(TAG, "compressVideo: " + fileVideoCompressedPath);


            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "Grabación de video cancelada por el usuario.", Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Error en la grabación del video.", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (requestCode == SELECT_FILE && null != data) {
            Log.i(TAG, "Se selecciona archivo de galeria");
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            //Log.d(TAG, "Galeria selected: " + selectedImage + ", " + filePathColumn);
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            photoGaleryPath = cursor.getString(columnIndex);
            Log.i(TAG, "De la galeria: " + photoGaleryPath);
            cursor.close();
            /*galeriaPrev.setVisibility(View.VISIBLE);*/

            //tv_videocapturadogal.setVisibility(View.VISIBLE);

            //Si hemos abierto correctamente la base de datos
            if (db != null) {
                /*db.execSQL("UPDATE Media SET galeriapath = '" + photoGaleryPath +"' WHERE idmedio == 1");
                Log.i(TAG, "UPDATE Media SET galeriapath = '" + photoGaleryPath +"' WHERE idmedio == 1");*/

                db.execSQL("INSERT INTO Media (medio, tipo) VALUES ('" + photoGaleryPath + "', galeria)");
                Log.v(TAG, "INSERT INTO Media (medio, tipo) VALUES ('" + photoGaleryPath +", galeria ");
            } else {
                Log.v(TAG, "No Hay base");
            }

            //////actualizaPrevios();


            if(photoGaleryPath != null){
                if(photoGaleryPath.endsWith("mp4")){
                    // galeriaPrev.setImageBitmap(BitmapFactory.decodeFile(photoGaleryPath));
                    // compress video

                    //galeriaPrev.setVisibility(View.VISIBLE);
                    /*tv_videocapturadogal.setVisibility(View.VISIBLE);*/
                    Log.d(TAG, "Video seleccionado de galeria: " + photoGaleryPath);

                    File f1 = new File(photoGaleryPath);
                    Log.i(TAG, "Path gal: " + f1.getPath().toString());

                    String[] paramsVideoGal = new String[]{photoGaleryPath, f1.getPath().toString(), f1.getPath().toString()};
                    VideoCompressGalleryAsyncTask videoCompressGalleryAsyncTask = new VideoCompressGalleryAsyncTask(getApplicationContext());
                    videoCompressGalleryAsyncTask.execute(paramsVideoGal);
                    photoGaleryPath = fileVideoGalCompressedPath;
                    Log.i(TAG, "compressVideo: " + photoGaleryPath);

                }else{
                    //btn_galeria.setImageBitmap(BitmapFactory.decodeFile(photoGaleryPath));
                    /*Glide.with(this).load(photoGaleryPath).into(btn_galeria);
                    tv_videocapturadogal.setVisibility(View.INVISIBLE);*/
                    Log.d(TAG, "Foto seleccionada de galeria: " + photoGaleryPath);
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Error en la captura de la imagen, por favor intente de nuevo.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error en la captura de la imagen.");
            }

        }
        else{
            Log.e(TAG, "error en camara!");
        }
        db.close();
    }

    private String getRealPathFromUri(Uri tempUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = this.getContentResolver().query(tempUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void actualizaPrevios() {
        userSQLiteHelper mediadbh =
                new userSQLiteHelper(getApplicationContext(), "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = mediadbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT medio, tipo FROM Media WHERE idmedio == 1", null);
        if (c.moveToFirst()) {
            Log.v(TAG, "hay medios");
            String fotoDB = c.getString(0);
            String videoDB = c.getString(1);
            String galeriaDB = c.getString(2);

            Log.i(TAG, "foto: " + photoPathDB + ", video: " + videoPathDB + ", gal: " + photoGaleryPathDB );
                /*imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(BitmapFactory.decodeFile(photoPathDB));
                galeriaPrev.setVisibility(View.VISIBLE);
                galeriaPrev.setImageBitmap(BitmapFactory.decodeFile(photoGaleryPathDB));
                db.close();*/

            Log.i(TAG, "Medios DB: " + photoPathDB + ", video: " + videoPathDB + ", gal: " + photoGaleryPathDB );
            if(fotoDB != null){
                Log.i(TAG, "hay foto db");
               /* imageCameraPreview.setVisibility(View.VISIBLE);*/
                /*btn_camara.setVisibility(View.INVISIBLE);*/
                ///btn_camara.setImageBitmap(BitmapFactory.decodeFile(fotoDB));
                //imageCameraPreview.setImageBitmap(BitmapFactory.decodeFile(fotoDB));
                /*Glide.with(this).load(fotoDB).into(imageCameraPreview);*/
            }
            else{
                Log.i(TAG, "no hay foto db");
                //btn_camara.setVisibility(View.INVISIBLE);
                /*btn_camara.setVisibility(View.VISIBLE);*/
            }

            if(videoDB != null){
                Log.i(TAG, "hay video db");
                //btn_video.setVisibility(View.INVISIBLE);
                /*imgvideoPrev.setVisibility(View.VISIBLE);*/
                /*tv_videocapturado.setVisibility(View.VISIBLE);*/
            }
            else{
                Log.i(TAG, "no hay video db");
                btn_video.setVisibility(View.VISIBLE);
                /*tv_videocapturado.setVisibility(View.INVISIBLE);*/
                /*imgvideoPrev.setVisibility(View.INVISIBLE);*/
            }

            if(galeriaDB != null){
                Log.i(TAG, "hay archivo galeria db");
               /* galeriaPrev.setVisibility(View.VISIBLE);*/
                if(galeriaDB.endsWith("mp4")){
                   /* btn_galeria.setVisibility(View.INVISIBLE);
                    galeriaPrev.setVisibility(View.VISIBLE);
                    galeriaPrev.setImageDrawable(getResources().getDrawable(R.drawable.boton_gris_circular));
                    tv_videocapturadogal.setVisibility(View.VISIBLE);*/
                }
                else{
                    /*btn_galeria.setVisibility(View.VISIBLE);
                    //galeriaPrev.setImageBitmap(BitmapFactory.decodeFile(galeriaDB));
                    Glide.with(this).load(galeriaDB).into(galeriaPrev);
                    tv_videocapturadogal.setVisibility(View.INVISIBLE);*/
                    //tv_videocapturadogal.setText("Imagen \n Seleccionada");
                }
            }
            else{
                Log.i(TAG, "no hay archivo galeria db");
                /*galeriaPrev.setVisibility(View.INVISIBLE);
                btn_galeria.setVisibility(View.VISIBLE);
                tv_videocapturadogal.setVisibility(View.INVISIBLE);*/
            }

        }
        else{
            Log.v(TAG, "NO hay MEDIOS");
        }
    }

    private Uri getImageUri(Reporte911Activity reportebotondelamujeractivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(reportebotondelamujeractivity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private String getPath(Uri selectedImaeUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(selectedImaeUri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            return cursor.getString(columnIndex);
        }
        return selectedImaeUri.getPath();
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver()
                .openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 72;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                .openInputStream(selectedImage), null, o2);
        return bitmap;
    }

    private void startDialog() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkPermission(Manifest.permission.CAMERA,Reporte911Activity.this)){
                Log.i(TAG, "hay permiso camara ok");
                openCameraApplication();
            }else{
                requestPermission(Reporte911Activity.this,new String[]{Manifest.permission.CAMERA},REQUEST_ACESS_CAMERA);
            }
        }else{
            Log.i(TAG, "abre directo camara ok");
            openCameraApplication();
        }
    }

    public static boolean checkPermission(String permission, Context context) {
        int statusCode = ContextCompat.checkSelfPermission(context, permission);
        return statusCode == PackageManager.PERMISSION_GRANTED;
    }

    private void openCameraApplication() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (picIntent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(picIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_OLDIE);
        }
    }

    public void SetUpViewPager (ViewPager viewpage){
        MyViewPageAdapter Adapter = new MyViewPageAdapter(getSupportFragmentManager());

        Adapter.AddFragmentPage(new Medios_1(), "Imagen");
        Adapter.AddFragmentPage(new Medios_2(), "Video");
        Adapter.AddFragmentPage(new Medios_3(), "Audio");

        /*
        You can add more Fragment Adapter
        But the minimum of the ViewPager is 3 index Page
         */
        //We Need Fragment class now

        viewpage.setAdapter(Adapter);

    }

    //Custom Adapter Here
    public class MyViewPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> MyFragment = new ArrayList<>();
        private List<String> MyPageTittle = new ArrayList<>();

        public MyViewPageAdapter(FragmentManager manager){
            super(manager);
        }

        public void AddFragmentPage(Fragment Frag, String Title){
            MyFragment.add(Frag);
            MyPageTittle.add(Title);
        }

        @Override
        public Fragment getItem(int position) {
            return MyFragment.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return MyPageTittle.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
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
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
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

    public class VideoCompressGalleryAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompressGalleryAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Reporte911Activity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Comprimiendo video...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            //dialog compressing...
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            fileVideoGalCompressedPath = null;
            String videoGalleryPath = params[0];
            String destinationPath = params[1];

            try {
                String destinationDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "";
                File tempFile = new File(photoGaleryPath);

                Log.i(TAG, "destination: " + destinationDir + ", path: " + tempFile.getPath());
                Log.i(TAG, "video a comprimir de galeria: " + videoGalleryPath + ", params2: " + destinationPath);
                fileVideoGalCompressedPath = SiliCompressor.with(mContext).compressVideo(videoGalleryPath, destinationDir);

                Log.d(TAG , "filePath : " + fileVideoGalCompressedPath);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return  fileVideoCompressedPath;
        }

        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            progressDialog.dismiss();
            File imageFile = new File(compressedFilePath);
            float length = imageFile.length() / 1024f; // Size in KB
            String value;
            if(length >= 1024)
                value = length/1024f+" MB";
            else
                value = length+" KB";
            Log.i(TAG, "Path: " + value);
        }
    }

    public class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompressAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Reporte911Activity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Comprimiendo video...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            //dialog compressing...
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            fileVideoCompressedPath = null;
            try {
                //String destinationUriString = "/storage/emulated/0/videokit";

                String destinationDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "";
                File tempFile = new File(videoPath);

                Log.i(TAG, "destination: " + destinationDir + ", path: " + tempFile.getPath());

                fileVideoCompressedPath = SiliCompressor.with(mContext).compressVideo(videoPath, destinationDir);

                Log.d(TAG , "filePath : " + fileVideoCompressedPath);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return  fileVideoCompressedPath;
        }

        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            progressDialog.dismiss();
            File imageFile = new File(compressedFilePath);
            float length = imageFile.length() / 1024f; // Size in KB
            String value;
            if(length >= 1024)
                value = length/1024f+" MB";
            else
                value = length+" KB";
            Log.i(TAG, "Path: " + value);
        }
    }

    @Override
    public void onBackPressed() { }
}
