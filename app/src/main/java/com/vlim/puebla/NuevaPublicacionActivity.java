package com.vlim.puebla;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.iceteck.silicompressorr.SiliCompressor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NuevaPublicacionActivity extends AppCompatActivity {

    TextView btn_nueva_publicacion, tv_videocapturado, tv_videocapturadogal;
    EditText et_titulopub, et_descripcionpub;
    String idusuario;
    JSONArray jsonArr;
    String JsonResponse = null;
    ProgressDialog progressDialog;
    ImageView btn_camara, btn_video, btn_galeria, Videoprev2;
    Uri selectedImage;
    Bitmap photo;
    /*private ProgressBar miprogress;
    private ObjectAnimator anim;*/
    Bitmap fotoBitmap, galeriaBitmap, videoBitmap;
    String titulopub, descripcionpub;
    // Camera activity request codes
    public static final int REQUEST_CODE_CAMERA = 23;
    public static final int SELECT_FILE = 3;
    public static final String TAG = "VLIMDEV";
    String photoPath = "", photoGaleryPath = "", videoPath = "";
    ImageView galeriaPrev;   //imageView
    VideoView videoPrev;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    String photoPathDB = "", videoPathDB = "", photoGaleryPathDB = "";

    /* Aproximacion 2*/
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Uri fileUri; // file url to store image/video

    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private ImageView imgPreview, imageCameraPreview;
    private VideoView vidPreview;
    private Button btnUpload;
    long totalSize = 0;
    // Toolbar
    TextView tv_titulo_toolbar;
    ImageView btn_back;
    private FileOutputStream fOut;
    String fileVideoCompressedPath = "", fileVideoGalCompressedPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_nueva_publicacion);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        btn_nueva_publicacion = (TextView) findViewById(R.id.btn_enviar911);
        btn_nueva_publicacion.setTypeface(tf);
        et_titulopub = (EditText) findViewById(R.id.et_nombreusr);
        et_descripcionpub = (EditText) findViewById(R.id.et_domicilio);
        btn_camara = (ImageView) findViewById(R.id.btn_ajustes_informacion);
        btn_galeria = (ImageView) findViewById(R.id.btn_contactos);
        btn_video = (ImageView) findViewById(R.id.btn_audio);

        // Toolbar
        tv_titulo_toolbar = (TextView) findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = (ImageView) findViewById(R.id.btn_back);

        //miprogress = (ProgressBar) findViewById(R.id.circularProgress);

        /* Aproximacion 2 */
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        ////btnUpload = (Button) findViewById(R.id.btnUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        /// --------------------------------------------------------------

        Intent i= getIntent();
        idusuario = i.getStringExtra("idusuario");

        /*imageView = (ImageView) findViewById(R.id.Imageprev);
        imageView.setVisibility(View.INVISIBLE);*/
        galeriaPrev = (ImageView) findViewById(R.id.Galeriaprev);
        galeriaPrev.setVisibility(View.INVISIBLE);
        videoPrev = (VideoView) findViewById(R.id.videoPreview);
        videoPrev.setVisibility(View.INVISIBLE);
        Videoprev2 = (ImageView) findViewById(R.id.Videoprev2);
        Videoprev2.setVisibility(View.INVISIBLE);
        imageCameraPreview = (ImageView) findViewById(R.id.imageCameraPreview);
        imageCameraPreview.setVisibility(View.INVISIBLE);
        tv_videocapturado = (TextView) findViewById(R.id.tv_audiocapturado);
        tv_videocapturado.setVisibility(View.INVISIBLE);
        tv_videocapturado.setTypeface(tf);
        tv_videocapturadogal = (TextView) findViewById(R.id.tv_videocapturadogal);
        tv_videocapturadogal.setVisibility(View.INVISIBLE);
        tv_videocapturadogal.setTypeface(tf);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            //readDataExternal();
        }

        btn_nueva_publicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titulopub = et_titulopub.getText().toString().trim();
                descripcionpub = et_descripcionpub.getText().toString().trim();

                if(titulopub.equalsIgnoreCase("")){
                    et_titulopub.setError("Ingresa el título de la publicación");
                }
                else if(descripcionpub.equalsIgnoreCase("")){
                    et_titulopub.setError("Ingresa la descripción de la publicación");
                }
                else{
                    // lee datos del usuario
                    userSQLiteHelper mediadbh =
                            new userSQLiteHelper(getApplicationContext(), "DBUsuarios", null, 1);
                    SQLiteDatabase db = mediadbh.getReadableDatabase();
                    Cursor c = db.rawQuery("SELECT photopath, videopath, galeriapath FROM Media", null);
                    if (c.moveToFirst()) {
                        Log.v(TAG, "hay MEDIOS");
                        photoPathDB = c.getString(0);
                        videoPathDB = c.getString(1);
                        photoGaleryPathDB = c.getString(2);

                        Log.i(TAG, "foto: " + photoPathDB + ", video: " + videoPathDB + ", gal: " + photoGaleryPathDB );
                        //db.close();

                        if((photoPathDB != null) || (videoPathDB != null) || (photoGaleryPathDB != null)){
                            Log.d(TAG, "Envia Denuncia con Archivos");
                            String[] params2 = new String[]{titulopub, descripcionpub, idusuario, photoPathDB, videoPathDB, photoGaleryPathDB};
                            Log.d(TAG, "antes de enviar: " + titulopub + ", " + descripcionpub + ", " + idusuario + ", " + photoPathDB +
                                    ", " + videoPathDB + ", " + photoGaleryPathDB);
                            borraMedios();
                            UploadFileToServer2 uploadFTS2 = new UploadFileToServer2();
                            uploadFTS2.execute(params2);

                        }
                    }
                    else{
                        Log.v(TAG, "NO hay MEDIOS");
                        // Envio sin medios
                        Log.d(TAG, "Envia Denuncia sin archivos");
                        String[] params2noFiles = new String[]{titulopub, descripcionpub, idusuario};
                        Log.d(TAG, "antes de enviar: " + titulopub + ", " + descripcionpub + ", " + idusuario);
                        UploadFileToServer2NoFiles uploadFTS2noFiles = new UploadFileToServer2NoFiles();
                        uploadFTS2noFiles.execute(params2noFiles);
                    }
                }
            }
        });

        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check Camera

                //if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermissionREAD_EXTERNAL_STORAGE(NuevaPublicacionActivity.this)) {
                    Log.i(TAG, "Permiso SD OK");

                    if (getApplicationContext().getPackageManager().hasSystemFeature(
                            PackageManager.FEATURE_CAMERA)) {
                        // Open default camera
                        int permissionCheck = ContextCompat.checkSelfPermission(NuevaPublicacionActivity.this, Manifest.permission.CAMERA);
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            Log.i(TAG, "abrir camera");
                            abrirCamara();
                        }
                        else{
                            Log.e(TAG, "Error, permiso camara: " + String.valueOf(permissionCheck));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                            }
                            else{
                                Log.i(TAG, "version oldie");
                            }
                        }

                    } else {
                        Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Log.i(TAG, "error en permisos");
                }
                //}else{
                //   abrirCamara();
                // }

            }
        });

        btn_galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent galeriaIntent = new Intent();
                galeriaIntent.setType("image/*");
                galeriaIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galeriaIntent, "Selecciona una imagen"), SELECT_FILE);*/

                // dialog preguntar imagen o video
                final CharSequence[] options = {"Imágenes", "Videos", "Cancelar"};
                AlertDialog.Builder builder = new AlertDialog.Builder(NuevaPublicacionActivity.this);
                builder.setTitle("Buscar...");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Imágenes")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(intent, SELECT_FILE);
                        } else if (options[item].equals("Videos")) {
                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(intent, SELECT_FILE);
                        } else if (options[item].equals("Cancelar")) {
                            dialog.dismiss();
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borraMedios();
                finish();
            }
        });
    }

    /**
     * Creating file uri to store image/video
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);
        //////intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file

        if (Build.VERSION.SDK_INT > 23) {
            intent.putExtra(Intent.EXTRA_STREAM, "content://" + fileUri);
        } else {
            intent.putExtra(Intent.EXTRA_STREAM, "file://" + fileUri);
        }

        // name
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void abrirCamara() {
        String fileName = System.currentTimeMillis()+".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    // Permiso aceptado
                    abrirCamara();
                else
                    // Permiso denegado
                    Toast.makeText(getApplicationContext(), "No se ha aceptado el permiso", Toast.LENGTH_SHORT).show();
                return;
            // Gestionar el resto de permisos
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Galeria fotos: " + requestCode + ", " + resultCode);

        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();

        Cursor c = db.rawQuery("SELECT photopath, videopath, galeriapath FROM Media WHERE idmedio == 1", null);
        if (c.moveToFirst()) {
            Log.i(TAG, "ya existen medios");
        }
        else{
            Log.v(TAG, "NO hay cosas, crear id 1");
            //Abrimos la base de datos 'DBUsuarios' en modo escritura
            /*userSQLiteHelper mediadbh =
                    new userSQLiteHelper(ReporteBotonDeLaMujerActivity.this, "DBUsuarios", null, 5);
            SQLiteDatabase db = mediadbh.getWritableDatabase();*/
            if (db != null) {
                //Insertamos los datos en la tabla Usuarios
                db.execSQL("INSERT INTO Media (idmedio) VALUES (1)");
            } else {
                Log.v(TAG, "No Hay base");
            }
        }
        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        /*userSQLiteHelper mediadbh =
                new userSQLiteHelper(NuevaPublicacionActivity.this, "DBUsuarios", null, 1);
        SQLiteDatabase db = mediadbh.getWritableDatabase();
        if (db != null) {
            //Insertamos los datos en la tabla Usuarios
            db.execSQL("INSERT INTO Media (idmedio) VALUES (1)");
        } else {
            Log.v(TAG, "No Hay base");
        }*/

        //if (resultCode == Activity.RESULT_OK) {
        if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                photoPath = getPath(fileUri);
                System.out.println("Image Path : " + photoPath);
                fotoBitmap = decodeUri(fileUri);
                //imageView.setVisibility(View.VISIBLE);
                /*btn_camara.setImageBitmap(photo);  //imageView
                btn_camara.setImageBitmap(fotoBitmap);*/
                Glide.with(this)
                        .load(photoPath)
                        //.diskCacheStrategy( DiskCacheStrategy.NONE )
                        //.skipMemoryCache( true )
                        .into(btn_camara);
                Log.d(TAG, "Foto capturada: " + photoPath);

                // Guarda en BD
                //Si hemos abierto correctamente la base de datos
                if (db != null) {
                    //Insertamos los datos en la tabla Usuarios
                    db.execSQL("UPDATE Media SET photopath = '" + photoPath +"' WHERE idmedio == 1");
                } else {
                    Log.v(TAG, "No Hay base");
                }

                actualizaPrevios();

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        else if(requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE && resultCode == RESULT_OK){

            Log.d(TAG, "Video capturado: " + videoPath);
            //btn_video.setEnabled(false);
            //btn_video.setVisibility(View.INVISIBLE);
            tv_videocapturado.setVisibility(View.VISIBLE);
            Uri videoCaptured = data.getData();
            String[] videoPathColumn = { MediaStore.Images.Media.DATA };
            //Log.d(TAG, "Video grabado: " + videoPathColumn);
            Videoprev2.setVisibility(View.VISIBLE);
            tv_videocapturado.setVisibility(View.VISIBLE);

            Cursor cursorvid = getContentResolver().query(videoCaptured,
                    videoPathColumn, null, null, null);
            cursorvid.moveToFirst();

            int columnIndex = cursorvid.getColumnIndex(videoPathColumn[0]);
            videoPath = cursorvid.getString(columnIndex);
            Log.d(TAG, "Video para cambiar nombre: " + videoPath);
            cursorvid.close();

            //Si hemos abierto correctamente la base de datos
            if (db != null) {
                db.execSQL("UPDATE Media SET videopath = '" + videoPath +"' WHERE idmedio == 1");
            } else {
                Log.v(TAG, "No Hay base");
            }

            actualizaPrevios();

            File f1 = new File(videoPath);
            NuevaPublicacionActivity.VideoCompressAsyncTask videoCompressAsyncTask = new NuevaPublicacionActivity.VideoCompressAsyncTask(getApplicationContext());
            videoCompressAsyncTask.execute(f1.getPath().toString(), f1.getPath().toString());
            Log.i(TAG, "compressVideo: " + fileVideoCompressedPath);

            // compress video

            if (resultCode == RESULT_OK) {

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "Grabación de video cancelada por el usuario.", Toast.LENGTH_SHORT)
                        .show();

            } else {
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
            //Log.d(TAG, " photogalerypath: " + photoGaleryPath );
            galeriaPrev.setVisibility(View.VISIBLE);

            //Si hemos abierto correctamente la base de datos
            if (db != null) {
                db.execSQL("UPDATE Media SET galeriapath = '" + photoGaleryPath +"' WHERE idmedio == 1");

                Log.i(TAG, "UPDATE Media SET galeriapath = '" + photoGaleryPath +"' WHERE idmedio == 1");
                //db.close();
            } else {
                Log.v(TAG, "No Hay base");
            }

            actualizaPrevios();

            //db.close();

            if(photoGaleryPath != null){
                if(photoGaleryPath.endsWith("mp4")){
                    // galeriaPrev.setImageBitmap(BitmapFactory.decodeFile(photoGaleryPath));
                    // compress video
                    tv_videocapturadogal.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Video seleccionado de galeria: " + photoGaleryPath);

                    File f1 = new File(photoGaleryPath);
                    Log.i(TAG, "Path gal: " + f1.getPath().toString());

                    String[] paramsVideoGal = new String[]{photoGaleryPath, f1.getPath().toString(), f1.getPath().toString()};
                    VideoCompressGalleryAsyncTask videoCompressGalleryAsyncTask = new VideoCompressGalleryAsyncTask(getApplicationContext());
                    videoCompressGalleryAsyncTask.execute(paramsVideoGal);
                    photoGaleryPath = fileVideoGalCompressedPath;
                    Log.i(TAG, "compressVideo: " + photoGaleryPath);

                }else{
                    ///galeriaPrev.setImageBitmap(BitmapFactory.decodeFile(photoGaleryPath));
                    Glide.with(this)
                            .load(photoGaleryPath)
                            /*.diskCacheStrategy( DiskCacheStrategy.NONE )
                            .skipMemoryCache( true )*/
                            .into(galeriaPrev);
                    Log.d(TAG, "Foto seleccionada de galeria: " + photoGaleryPath);
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Error en la captura de la imagen, por favor intente de nuevo.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error en la captura de la imagen.");
            }

        }

        else{
            Log.e(TAG, "error en camara!. NuevPub");
        }
    }

    private void actualizaPrevios() {
        userSQLiteHelper mediadbh =
                new userSQLiteHelper(getApplicationContext(), "DBUsuarios", null, 1);
        SQLiteDatabase db = mediadbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT photopath, videopath, galeriapath FROM Media WHERE idmedio == 1", null);
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
                imageCameraPreview.setVisibility(View.VISIBLE);
                btn_camara.setVisibility(View.INVISIBLE);
                ///btn_camara.setImageBitmap(BitmapFactory.decodeFile(fotoDB));
                ///imageCameraPreview.setImageBitmap(BitmapFactory.decodeFile(fotoDB));
                Glide.with(this)
                        .load(fotoDB)
                        /*.diskCacheStrategy( DiskCacheStrategy.NONE )
                        .skipMemoryCache( true )*/
                        .into(imageCameraPreview);
            }
            else{
                Log.i(TAG, "no hay foto db");
                //btn_camara.setVisibility(View.INVISIBLE);
                btn_camara.setVisibility(View.VISIBLE);
            }

            if(videoDB != null){
                Log.i(TAG, "hay video db");
                //btn_video.setVisibility(View.INVISIBLE);
                Videoprev2.setVisibility(View.VISIBLE);
                tv_videocapturado.setVisibility(View.VISIBLE);
            }
            else{
                Log.i(TAG, "no hay video db");
                btn_video.setVisibility(View.VISIBLE);
                tv_videocapturado.setVisibility(View.INVISIBLE);
                Videoprev2.setVisibility(View.INVISIBLE);
            }

            if(galeriaDB != null){
                Log.i(TAG, "hay archivo galeria db");
                galeriaPrev.setVisibility(View.VISIBLE);
                if(galeriaDB.endsWith("mp4")){
                    btn_galeria.setVisibility(View.INVISIBLE);
                    galeriaPrev.setVisibility(View.VISIBLE);
                    galeriaPrev.setImageDrawable(getResources().getDrawable(R.drawable.boton_naranja_circular));
                    tv_videocapturadogal.setVisibility(View.VISIBLE);
                }
                else{
                    btn_galeria.setVisibility(View.VISIBLE);
                    ///galeriaPrev.setImageBitmap(BitmapFactory.decodeFile(galeriaDB));
                    Glide.with(this)
                            .load(galeriaDB)
                            /*.diskCacheStrategy( DiskCacheStrategy.NONE )
                            .skipMemoryCache( true )*/
                            .into(galeriaPrev);
                    tv_videocapturadogal.setVisibility(View.INVISIBLE);
                    //tv_videocapturadogal.setText("Imagen \n Seleccionada");
                }
            }
            else{
                Log.i(TAG, "no hay archivo galeria db");
                galeriaPrev.setVisibility(View.INVISIBLE);
                btn_galeria.setVisibility(View.VISIBLE);
                tv_videocapturadogal.setVisibility(View.INVISIBLE);
            }

        }
        else{
            Log.v(TAG, "NO hay MEDIOS");
        }
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
        o.inSampleSize = 8;

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
        o2.inSampleSize = 8;

        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver()
                .openInputStream(selectedImage), null, o2);
        return bitmap;
    }

    private boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permiso necesario");
        alertBuilder.setMessage(msg + " es necesario habilitar el permiso");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private class UploadFileToServer2 extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(NuevaPublicacionActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Enviando Publicación. Por favor espere.");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();

            //
            // setting progress bar to zero
            progressBar.setProgress(0);
            //super.onPreExecute();

            et_titulopub.setEnabled(false);
            et_titulopub.setFocusable(false);
            et_descripcionpub.setEnabled(false);
            et_descripcionpub.setFocusable(false);
            btn_nueva_publicacion.setEnabled(false);
            btn_nueva_publicacion.getBackground().setAlpha(100);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "% completado");
        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... params) {

            String titulo2 = params[0];
            String descripcion2 = params[1];
            String idusr2 = params[2];
            String photoPathDB2 = params[3];
            String videoPathDB2 = params[4];
            String photoGaleryPathDB2 = params[5];
            String responseString2 = null;
            ByteArrayOutputStream baos, baosGal;

            Log.i(TAG, "prepara: titulo: " + titulo2 +", descrp:" + descripcion2 + ", idusr: " + idusr2 + ", photo: " +
                    photoPathDB2 + ", gal: " + videoPathDB2 + ", vid: " + photoGaleryPathDB2);

            HttpClient httpclient2 = new DefaultHttpClient();
            HttpPost httppost2 = new HttpPost(Config.NUEVA_PUBLICACION_URL);

            try {
                AndroidMultiPartEntity entity2 = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                // Adding file data to http body
                ///Log.i(TAG, "tipos archivos, foto: " + photoPath + ", video: " + videoPath + ", galeria: " + photoGaleryPath);

                if(photoPathDB2 != null) {

                    fotoBitmap = BitmapFactory.decodeFile(photoPathDB2);
                    Log.i(TAG, "fotobitmap: " + fotoBitmap);

                    try {
                        String filename1 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                        Log.i(TAG, "hora: " + filename1);
                        //String path = Environment.getExternalStorageDirectory().toString();
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                        File fileFoto = new File(path, "/" + filename1 + ".png");
                        FileOutputStream fileOutputStream = new FileOutputStream(fileFoto);
                        fotoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        entity2.addPart("file", new FileBody(fileFoto));
                    }catch (FileNotFoundException e) {
                        Log.e(TAG, e.getMessage(), e);

                    }catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error: " + e.getMessage());
                    }
                }else{
                    Log.i(TAG, "No hay imagen de camara");
                    //entity2.addPart("imagen", new StringBody("."));
                }
                if(videoPathDB2 != null){
                    /*Log.i(TAG, "VIDEO path: " + videoPath);
                    File sourceVideo = new File(videoPath);
                    entity2.addPart("file", new FileBody(sourceVideo));*/

                    /*Log.i(TAG, "VIDEO path: " + videoPathDB2);
                    File sourceVideo = new File(videoPathDB2);
                    entity2.addPart("file", new FileBody(sourceVideo));*/

                    Log.i(TAG, "VIDEO path original: " + videoPathDB2);
                    File sourceVideo = new File(fileVideoCompressedPath);
                    entity2.addPart("file", new FileBody(sourceVideo));

                }else{
                    Log.i(TAG, "No hay video");
                    entity2.addPart("video", new StringBody("."));
                }

                if(photoGaleryPathDB2 != null){
                    Log.i(TAG, "hay imagen o video de galeria");

                    if(photoGaleryPathDB2.endsWith("mp4")){
                        Log.i(TAG, "por enviar video de galeria");

                        File sourceVideoGal = new File(fileVideoGalCompressedPath);
                        entity2.addPart("file", new FileBody(sourceVideoGal));

                        /*File fileGal = new File(photoGaleryPathDB2);
                        entity2.addPart("file", new FileBody(sourceGalVideo));*/
                    }
                    else{
                        galeriaBitmap = BitmapFactory.decodeFile(photoGaleryPathDB2);
                        Log.i(TAG, "Galeria path: " + photoGaleryPathDB2);

                        String filename = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

                        try{
                            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
                            File fileGal = new File(path, "/" + filename + ".png");

                            FileOutputStream fileOutputStream = new FileOutputStream( fileGal );
                            galeriaBitmap.compress( Bitmap.CompressFormat.JPEG, 100, fileOutputStream );
                            fileOutputStream.flush();
                            fileOutputStream.close();
                            Log.i(TAG, "Galeria file: " + fileGal);

                            entity2.addPart("file", new FileBody(fileGal));

                        }catch (FileNotFoundException e) {
                            Log.e(TAG, e.getMessage(), e);

                        }catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Error: " + e.getMessage());
                        }
                    }



                }
                else{
                    Log.e(TAG, "No hay imagen de galeria");
                    entity2.addPart("imagen", new StringBody("."));
                }

                entity2.addPart("titulo", new StringBody(titulo2, Charset.forName("UTF-8")));
                entity2.addPart("descripcion", new StringBody(descripcion2, Charset.forName("UTF-8")));
                entity2.addPart("idusr", new StringBody(idusr2));

                totalSize = entity2.getContentLength();
                httppost2.setEntity(entity2);

                Log.i(TAG, "Send: " + entity2.getContentType().getName());
                Log.i(TAG, "Total size: " + totalSize/1048576 + " MB");

                // Making server call
                HttpResponse response = httpclient2.execute(httppost2);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString2 = EntityUtils.toString(r_entity);
                } else {
                    responseString2 = "Error occurred! Http Status Code: "
                            + statusCode;
                    Log.e(TAG, responseString2);
                }
            }catch (ClientProtocolException e) {
                responseString2 = e.toString();
                Log.e(TAG, responseString2);
            }catch (IOException e) {
                responseString2 = e.toString();
                Log.e(TAG, responseString2);
            }
            return responseString2;
        }


        /*private String uploadFile2(String titulo, String descripcion, String idusr) {
        }*/
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "Response from server: " + result);
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }


            // JSON respuesta  //[{"respuesta":"OK","mensaje":"Se registro bien la publicación"}]
            try {
                JSONArray jsonRespuesta = new JSONArray(result);
                String respuesta = jsonRespuesta.getJSONObject(0).getString("respuesta");
                Log.i(TAG, "respuesta: " + respuesta);
                if(respuesta.equals("OK")){
                    Toast.makeText(getApplicationContext(), "Publicación registrada correctamente", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error! Tu publicación no ha sido guardada.", Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error envio: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error en el envio.", Toast.LENGTH_LONG).show();
                finish();

            }

            super.onPostExecute(result);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null) {

            if(progressDialog.isShowing())
                progressDialog.dismiss();

            progressDialog = null;
        }
    }

    private class UploadFileToServer2NoFiles extends AsyncTask<String, Integer, String>{
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(NuevaPublicacionActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Enviando Publicación. Por favor espere.");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            // setting progress bar to zero
            progressBar.setProgress(0);
            //super.onPreExecute();

            et_titulopub.setEnabled(false);
            et_titulopub.setFocusable(false);
            et_descripcionpub.setEnabled(false);
            et_descripcionpub.setFocusable(false);
            btn_nueva_publicacion.setEnabled(false);
            btn_nueva_publicacion.getBackground().setAlpha(100);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "% completado");
        }

        @SuppressWarnings("deprecation")
        protected String doInBackground(String... params) {
            String titulo2 = params[0];
            String descripcion2 = params[1];
            String idusr2 = params[2];
            String responseString2 = null;

            Log.i(TAG, "prepara: titulo: " + titulo2 +", descrp:" + descripcion2 + ", idusr: " + idusr2);

            HttpClient httpclientNoFiles = new DefaultHttpClient();
            HttpPost httppostNoFiles = new HttpPost(Config.NUEVA_PUBLICACION_SINMEDIOS_URL);

            try {
                AndroidMultiPartEntity entity2 = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                // Adding file data to http body
                ///Log.i(TAG, "tipos archivos, foto: " + photoPath + ", video: " + videoPath + ", galeria: " + photoGaleryPath);



                entity2.addPart("titulo", new StringBody(titulo2, Charset.forName("UTF-8")));
                entity2.addPart("descripcion", new StringBody(descripcion2, Charset.forName("UTF-8")));
                entity2.addPart("idusr", new StringBody(idusr2));

                totalSize = entity2.getContentLength();
                httppostNoFiles.setEntity(entity2);

                Log.i(TAG, "Send: " + entity2.getContentType().getName());
                Log.i(TAG, "Total size: " + totalSize/1048576 + " MB");

                // Making server call
                HttpResponse response = httpclientNoFiles.execute(httppostNoFiles);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString2 = EntityUtils.toString(r_entity);
                } else {
                    responseString2 = "Error occurred! Http Status Code: "
                            + statusCode;
                    Log.e(TAG, responseString2);
                }
            }catch (ClientProtocolException e) {
                responseString2 = e.toString();
                Log.e(TAG, responseString2);
            }catch (IOException e) {
                Log.e(TAG, responseString2);
                responseString2 = e.toString();
            }
            return responseString2;
        }


        /*private String uploadFile2(String titulo, String descripcion, String idusr) {
        }*/
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "Response from server: " + result);
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }


            // JSON respuesta  //[{"respuesta":"OK","mensaje":"Se registro bien la publicación"}]
            try {
                JSONArray jsonRespuesta = new JSONArray(result);
                String respuesta = jsonRespuesta.getJSONObject(0).getString("respuesta");
                Log.i(TAG, "respuesta: " + respuesta);
                if(respuesta.equals("OK")){
                    Toast.makeText(getApplicationContext(), "Publicación registrada correctamente", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error! Tu publicación no ha sido guardada.", Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error envio: " + e.getMessage());
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error en el envio.", Toast.LENGTH_LONG).show();
                finish();

            }

            super.onPostExecute(result);
        }
    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() { }

    private void borraMedios() {
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, 1);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        db.delete("Media", null, null);
        db.close();
        Log.i(TAG, "Borrando bd medios");
    }

    public class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompressAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(NuevaPublicacionActivity.this);
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

    public class VideoCompressGalleryAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;

        public VideoCompressGalleryAsyncTask(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(NuevaPublicacionActivity.this);
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


}
