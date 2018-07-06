package com.vlim.puebla;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
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
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AjustesChatActivity extends AppCompatActivity {

    String idusuario = "", nick = "", nombreUsuario = "", img = "";
    EditText et_nickname;
    CircleImageView img_usuario;
    TextView tv_nicknamemsg, btn_cambiarnickname;
    String TAG = "PUEBLA";
    JSONArray jsonArr;
    String JsonResponse = null;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int REQUEST_CODE_CAMERA = 23;
    private Uri fileUri; // file url to store image/video
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;  // captura fotografia
    /*public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;*/
    public static final int SELECT_FILE = 3;    // Selecciona imagen de galeria
    String photoPath = "", photoGaleryPath = "";
    Bitmap fotoBitmap, galeriaBitmap;
    private ProgressBar progressBar;
    long totalSize = 0;
    private TextView txtPercentage;
    String responseStringUpdateImg = null;
    // Toolbar
    TextView tv_titulo_toolbar;
    ImageView btn_back;
    ProgressDialog progressDialog;
    Integer puedeRegistrar = 0;
    String usr  = null, pass = null, nombreFoto, newusrname;
    private static final int REQUEST_ACESS_CAMERA = 2;
    private static final int REQUEST_ACESS_STORAGE = 3;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE_OLDIE = 1010;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_ajustes_chat);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        // Toolbar
        tv_titulo_toolbar = (TextView) findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = (ImageView) findViewById(R.id.btn_back);

        et_nickname = (EditText) findViewById(R.id.et_nickname);
        et_nickname.setTypeface(tf);
        img_usuario = (CircleImageView) findViewById(R.id.img_usuario);
        btn_cambiarnickname = (TextView) findViewById(R.id.btn_cambiarnickname);
        btn_cambiarnickname.setTypeface(tf);
        tv_nicknamemsg = (TextView) findViewById(R.id.tv_nicknamemsg);
        tv_nicknamemsg.setTypeface(tf);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        txtPercentage.setTypeface(tf);
        progressBar = (ProgressBar) findViewById(R.id.progressBar911);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            //readDataExternal();
        }

        //et_nickname.setFocusableInTouchMode(true);
        et_nickname.setSelected(false);

        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, 1);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        //Cursor c = db.query("Usuarios", campos, "idusuario=?", args, null, null, null);
        Cursor c = db.rawQuery("SELECT idusuario, nick, img FROM Usuarios", null);
        Log.i(TAG, "cols: " + c.getCount());

        if (c.moveToFirst()) {

            /*while (c.moveToNext()) {
                idusuario = c.getString(0);
                nick = c.getString(1);
                img = c.getString(2);
                Log.i(TAG, "id: " + idusuario + ", nick: " + nick + ", img: " + img);
            }*/

            Log.v(TAG, "hay cosas settings");
            idusuario = c.getString(0);
            nick = c.getString(1);
            img = c.getString(2);
            Log.d(TAG, "Leyendo Base ID: " + idusuario + ", nickname: " + nick + ", foto nick: " + img);
            et_nickname.setText(nick);
            int textLength = et_nickname.getText().length();
            et_nickname.setSelection(textLength, textLength);
            img_usuario.refreshDrawableState();

            Picasso.Builder builder = new Picasso.Builder(this);
            builder.listener(new Picasso.Listener()
            {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                    Log.e(TAG, "Error picasso: " + exception.getMessage());
                }
            });
            builder.build().load(img.trim()).into(img_usuario);
            
        }
        else{
            Log.v(TAG, "NO hay cosas settings");
        }

        db.close();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_cambiarnickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newusrname = et_nickname.getText().toString().trim();
                if(newusrname.length()>0){
                    Log.d(TAG, "prepara: idusr: " + idusuario + ", newusrname: " + newusrname);
                    et_nickname.setText("");
                    preparaUpdate(idusuario, newusrname);
                    //actualizaImgUsuario();
                }
                else{
                    et_nickname.setError("Escribe tu nickname.");
                }

            }
        });

        img_usuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open dialog
                opcionesImagen();
            }
        });
    }

    private void opcionesImagen() {
        List<String> listOpciones = new ArrayList<String>();
        listOpciones.add("Tomar fotografía");
        listOpciones.add("Elegir imagen de galería");
        listOpciones.add("Cancelar");
        //Create sequence of items
        final CharSequence[] OpcionesImagen = listOpciones.toArray(new String[listOpciones.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Cambiar Imagen");
        dialogBuilder.setItems(OpcionesImagen, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if(item == 0){
                    Log.i(TAG, "Toma foto");
                    abrirCamara();
                }
                else if(item == 1){
                    Log.i(TAG, "Elige de galeria");
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, SELECT_FILE);
                }
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
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
           /* if (ContextCompat.checkSelfPermission(DenunciaAnonimaActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_CAMERA_OLDIE);
            }*/

            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this)) {
                Log.i(TAG, "aquii....**");
                startDilog();
            }else{
                requestPermission(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_ACESS_STORAGE);
            }

        }
    }

    public static boolean checkPermission(String permission, Context context) {
        int statusCode = ContextCompat.checkSelfPermission(context, permission);
        return statusCode == PackageManager.PERMISSION_GRANTED;
    }
    public static void requestPermission(AppCompatActivity activity, String[] permission, int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission[0])) {
            Toast.makeText(activity, "Application need permission", Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(activity, permission, requestCode);
    }

    private void startDilog() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkPermission(Manifest.permission.CAMERA,AjustesChatActivity.this)){
                Log.i(TAG, "hay permiso camara ok");
                openCameraApplication();
            }else{
                requestPermission(AjustesChatActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_ACESS_CAMERA);
            }
        }else{
            Log.i(TAG, "abre directo camara ok");
            openCameraApplication();
        }
    }

    private void openCameraApplication() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (picIntent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(picIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE_OLDIE);
        }
    }

    private void preparaUpdate(String idusuario, String newusrname) {
        btn_cambiarnickname.setEnabled(false);
        et_nickname.setEnabled(false);
        et_nickname.setFocusable(false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("idusr" , idusuario);
            post_dict.put("newusrname", newusrname);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            Log.v(TAG, "postdic len: " + String.valueOf(post_dict));
            //new SendJsonDataToServer().execute(String.valueOf(post_dict));
            updateNickname(String.valueOf(post_dict));
        }
    }

    private void updateNickname(String params) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String idusr = jsonObject.getString("idusr");
            newusrname = jsonObject.getString("newusrname");
            jsonBody.put("idusr", idusr);
            jsonBody.put("newusrname", newusrname);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UPDATE_NICKNAME_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.i("VOLLEYresponse", String.valueOf(jsonArr));
                    Log.i(TAG, "VOLLEY response: " + jsonArr);

                    for (int i = 0; i < jsonArr.length(); i++){
                        try {
                            JSONObject jsonObj = jsonArr.getJSONObject(i);
                            Log.i("log_tag","respuesta: "+jsonObj.getString("respuesta") +
                                    ", mensaje: "+jsonObj.getString("mensaje")
                            );
                            if(jsonObj.getString("respuesta").equals("OK")){
                                //////Toast.makeText(getApplicationContext(), "Nickname Actualizado.", Toast.LENGTH_LONG).show();
                                // actualiza bd
                                userSQLiteHelper usdbh =
                                        new userSQLiteHelper(getApplicationContext(), "DBUsuarios", null, 5);
                                SQLiteDatabase db = usdbh.getReadableDatabase();
                                if (db != null) {
                                    db.execSQL("UPDATE Usuarios SET nick = '" + newusrname +"' WHERE idusuario == '" + idusuario + "'");
                                    Log.i(TAG, "UPDATE Usuarios SET nick = '" + newusrname +"' WHERE idusuario == '" + idusuario + "'");
                                    //Cerramos la base de datos
                                    db.close();
                                } else {
                                    Log.v(TAG, "No Hay base");
                                }
                                showAlert("Nickname Actualizado. \nRecuerda que tu nickname también es tu nombre de usuario.");
                                //finish();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Error, tu Nickname no ha sido actualizado.", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "VOLLEY: " + error.toString());
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
        builder.setMessage(message).setTitle("Aviso")
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Medio capturado: " + requestCode + ", " + resultCode);

       /* if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE){
            try {
                photoPath = getPath(fileUri);
                System.out.println("Image Path : " + photoPath);
                fotoBitmap = decodeUri(fileUri);
                //img_usuario.setImageBitmap(fotoBitmap);
                Glide.with(this).load(fotoBitmap).into(img_usuario);
                Log.d(TAG, "Foto capturada: " + photoPath);
                actualizaImgUsuario();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }*/
        if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE){
            Log.i(TAG, "Imagen capturada");

            try {
                photoPath = getPath(fileUri);
                fotoBitmap = decodeUri(fileUri);
                //Glide.with(this).load(fotoBitmap).into(imageView);
                Glide.with(this).load(photoPath).into(img_usuario);
                Log.d(TAG, "Foto capturada: " + photoPath);
                actualizaImgUsuario();

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode != RESULT_OK) {
            Log.e(TAG, "error en captura de fotoo!");
        }


        if (requestCode == SELECT_FILE && null != data) {

            Log.i(TAG, "Se selecciona archivo de galeria");
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            //Log.d(TAG, "Galeria selected: " + selectedImage + ", " + filePathColumn);
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            photoPath = cursor.getString(columnIndex);
            cursor.close();
            //img_usuario.setImageBitmap(BitmapFactory.decodeFile(photoPath));
            Glide.with(this).load(photoPath).into(img_usuario);
            Log.d(TAG, "Foto seleccionada de galeria: " + photoPath);
            actualizaImgUsuario();
        }
        /*else{
            Toast.makeText(getApplicationContext(), "Error en la captura de la imagen, por favor intente de nuevo.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error en la captura de la imagen.");
        }*/

        if(requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE_OLDIE){
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Imagen capturada oldie");
                //Log.e(TAG, "PATH oldie: " + photoPath);

                if (data.hasExtra("data")) {
                    Log.e(TAG, "Hay extras: ");
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                    fileUri = getImageUri(AjustesChatActivity.this, bitmap);
                    File finalFile = new File(getRealPathFromUri(fileUri));
                    img_usuario.setVisibility(View.VISIBLE);
                    //btn_camara.setVisibility(View.INVISIBLE);
                    //imageView.setImageBitmap(bitmap);
                    //Glide.with(this).load(bitmap).into(imageView);
                    Log.e(TAG, "finalFile: " + finalFile.getPath());

                    photoPath = getPath(fileUri);
                    System.out.println("Image Path : " + photoPath);



                } else if (data.getExtras() == null) {
                    Log.e(TAG, "nulos extras");
                    Toast.makeText(getApplicationContext(),
                            "No extras to retrieve!", Toast.LENGTH_SHORT)
                            .show();
                }
                else{
                    Log.e(TAG, "no extras");
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Cámara cancelada",
                        Toast.LENGTH_SHORT).show();
            }
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

    private Uri getImageUri(AjustesChatActivity denunciaanonimaactivity, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(denunciaanonimaactivity.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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

    private void actualizaImgUsuario() {
        // Enviando parametros 911 Archivos
        String[] paramsImagenUsuario = new String[]{photoPath, idusuario};
        Log.i(TAG, "prepara ActualizaImg: idusr: " + idusuario +  ", photo: " + photoPath);
        UploadImagenUsuario uploadImagenUsuario = new UploadImagenUsuario();
        uploadImagenUsuario.execute(paramsImagenUsuario);
    }

    @Override
    public void onBackPressed() { }

    private class UploadImagenUsuario extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(AjustesChatActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando información. Por favor espere.");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();

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

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "% completado");
        }

        @Override
        protected String doInBackground(String... params) {

            String photoPath = params[0];
            String idusuario = params[1];
            //String responseString2 = null;
            ByteArrayOutputStream baos, baosGal;

            Log.i(TAG, "preparaArch: idusr: " + idusuario + ", photo: " + photoPath );

            HttpClient httpclientUpdateImgarch = new DefaultHttpClient();
            HttpPost httppostUpdateImgarch = new HttpPost(Config.UPDATE_IMAGEN_URL);

            try {
                AndroidMultiPartEntity entity911arch = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                if(photoPath != "") {
                    Log.i(TAG, "Foto path: " + photoPath);

                    try {
                        /*String filename1 = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                        Log.i(TAG, "hora: " + filename1);
                        String path = Environment.getExternalStorageDirectory().toString();
                        File fileFoto = new File(path, "/EscYuc/" + filename1 + ".png");*/
                        fotoBitmap = BitmapFactory.decodeFile(photoPath);
                        String filename = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
                        File fileFoto = new File(path, "/" + filename + ".png");

                        nombreFoto = filename + ".png";
                        Log.i(TAG, "foto va: " + fileFoto);
                        FileOutputStream fileOutputStream = new FileOutputStream(fileFoto);
                        fotoBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
                        fileOutputStream.flush();
                        fileOutputStream.close();

                        entity911arch.addPart("file", new FileBody(fileFoto));
                    }catch (FileNotFoundException e) {
                        Log.e(TAG, e.getMessage(), e);

                    }catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Error: " + e.getMessage());
                    }
                }

                entity911arch.addPart("idusr", new StringBody(idusuario));

                totalSize = entity911arch.getContentLength();
                httppostUpdateImgarch.setEntity(entity911arch);

                Log.i(TAG, "Total size: " + totalSize/1048576 + " MB");

                HttpResponse response = httpclientUpdateImgarch.execute(httppostUpdateImgarch);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseStringUpdateImg = EntityUtils.toString(r_entity);
                } else {
                    responseStringUpdateImg = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            }catch (ClientProtocolException e) {
                responseStringUpdateImg = e.toString();
            }catch (IOException e) {
                responseStringUpdateImg = e.toString();
            }
            return responseStringUpdateImg;
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
            }
            if(fotoBitmap != null){
                fotoBitmap.recycle();
            }

            Log.i(TAG, "Response from server: " + result);
            // JSON respuesta  //[{"respuesta":"OK","mensaje":"Se registro bien la publicación"}]
            try {
                JSONArray jsonRespuesta = new JSONArray(result);
                String respuesta = jsonRespuesta.getJSONObject(0).getString("respuesta");
                String mensaje = jsonRespuesta.getJSONObject(0).getString("mensaje");

                Log.i(TAG, "respuesta: " + respuesta);
                if(respuesta.equals("OK")){
                    //showAlert(result);
                    //Toast.makeText(getApplicationContext(), "Publicación registrada correctamente", Toast.LENGTH_LONG).show();

                    //finish();
                    btn_cambiarnickname.setEnabled(true);
                    et_nickname.setEnabled(true);
                    et_nickname.setFocusable(true);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    // actualiza bd
                    userSQLiteHelper usdbh =
                            new userSQLiteHelper(getApplicationContext(), "DBUsuarios", null, 5);
                    SQLiteDatabase db = usdbh.getWritableDatabase();
                    if (db != null) {
                        db.execSQL("UPDATE Usuarios SET img = ' http://187.217.215.234:8080/Escudo_Yucatan/complementos/imagenesperfil/" + nombreFoto +"' WHERE idusuario == '" + idusuario + "' ");
                    } else {
                        Log.v(TAG, "No Hay base");
                    }

                    showAlert("La imagen de su cuenta ha sido actualizada.");

                    //finish();
                    //startActivity(getIntent());
                }
                else{
                    Toast.makeText(getApplicationContext(), "Error! Tu publicación no ha sido guardada.", Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error envio: " + e.getMessage());
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(), "Error en el envio.", Toast.LENGTH_LONG).show();
                finish();
            }
            super.onPostExecute(result);
        }
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

}
