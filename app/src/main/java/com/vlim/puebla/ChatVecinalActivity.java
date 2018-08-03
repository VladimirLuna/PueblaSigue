package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChatVecinalActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    RecyclerView recyclerView;
    List<ModelPublicaciones> listPublicaciones = new ArrayList<>();
    RecyclerViewAdapter adapter;
    LinearLayoutManager llm;
    TextView btn_nueva_publicacion;
    String idusuario = "";
    TextView tv_nuevapublicacion;
    ImageView img_settings, img_msjprivado;
    NetworkConnection nt_check;

    // Toolbar
    TextView tv_titulo_toolbar;
    ImageView btn_back;
    int NUEVA_PUBLICACION_REQUEST = 1200;

    private SwipeRefreshLayout swipeRefreshLayout;
    public static final String TAG = "PUEBLA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_vecinal);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //video_pub = (ImageView) findViewById(R.id.img_video);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);

        btn_nueva_publicacion = (TextView) findViewById(R.id.btn_enviarreporte);
        img_msjprivado = (ImageView) findViewById(R.id.img_msjprivado);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        // Toolbar
        tv_titulo_toolbar = (TextView) findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = (ImageView) findViewById(R.id.btn_back);

        tv_nuevapublicacion = (TextView) findViewById(R.id.btn_enviarreporte);
        tv_nuevapublicacion.setTypeface(tf);

        recyclerView = findViewById(R.id.recyclerView);
        //btn_publicar = (Button) findViewById(R.id.btn_publicar);

        Intent i= getIntent();
        idusuario = i.getStringExtra("idusuario");
        //Toast.makeText(getApplicationContext(), "Id: " + idusuario, Toast.LENGTH_LONG).show();

        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(ChatVecinalActivity.this);

        muestraMensaje();

        img_settings = (ImageView) findViewById(R.id.img_settings);
        img_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(ChatVecinalActivity.this, ChatSettingsActivity.class);
                startActivity(settingIntent);
            }
        });

        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("idusuario", idusuario);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            //Log.v(TAG, String.valueOf(post_dict));
            new FetchPublicacionesInfo().execute(String.valueOf(post_dict));
        }

        btn_nueva_publicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Comprueba conexion a Internet
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    Intent nuevaPublicacionIntent = new Intent(ChatVecinalActivity.this, NuevaPublicacionActivity.class);
                    Bundle datosUsuario = new Bundle();
                    datosUsuario.putString("idusuario", idusuario);
                    nuevaPublicacionIntent.putExtras(datosUsuario);
                    //startActivity(nuevaPublicacionIntent);
                    startActivityForResult(nuevaPublicacionIntent, NUEVA_PUBLICACION_REQUEST);
                }else{
                    Toast.makeText(getApplicationContext(), "Se requiere conexión a Internet.", Toast.LENGTH_LONG).show();
                }

            }
        });

        img_msjprivado.setVisibility(View.INVISIBLE);

        img_msjprivado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent msjPrivadoIntent = new Intent(ChatVecinalActivity.this, TodosMensajesPrivadosActivity.class);
                Bundle datosUsuario = new Bundle();
                datosUsuario.putString("idusuario", idusuario);
                msjPrivadoIntent.putExtras(datosUsuario);
                startActivity(msjPrivadoIntent);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void muestraMensaje() {
        new AlertDialog.Builder(ChatVecinalActivity.this)
                .setTitle("Chat vecinal")
                .setMessage("Esta funcionalidad sólo es con fines preventivos, no para emergencias.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getApplicationContext(), "Lista de publicaciones actualizada", Toast.LENGTH_LONG).show();
        startActivity(getIntent());
        finish();
    }

    /**
     * Async task class to get json response by making HTTP call
     * Async task class is used because you cannot create a network connection on main thread
     */
    public class FetchPublicacionesInfo extends AsyncTask<String, Void, Void> {
        ProgressDialog progressDialog;
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ChatVecinalActivity.this);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Leyendo publicaciones, por favor espere");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            /*
            creatingURLConnection is a function use to establish connection
            */
            String JsonResponse = null;
            String JsonDATA = params[0];

            response = creatingURLConnection(JsonDATA);

            Log.v(TAG, response);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            //Toast.makeText(getApplicationContext(),"Connection successful.",Toast.LENGTH_SHORT).show();

            try {
                if (response != null && !response.equals("")) {
                    JSONArray responseArray = new JSONArray(response);
                    if (responseArray != null && responseArray.length() > 0) {
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject battleObj = responseArray.getJSONObject(i);
                            ModelPublicaciones modelPublicaciones = new ModelPublicaciones();
                            modelPublicaciones.setTitulo(battleObj.optString("titulo"));
                            modelPublicaciones.setDescripcion(battleObj.optString("descripcion"));
                            modelPublicaciones.setFecha(battleObj.optString("fecha_publicacion"));
                            modelPublicaciones.setNComentarios(battleObj.optString("nComentarios"));
                            modelPublicaciones.setIdPublicacion(battleObj.optString("id_publicacion"));
                            if (battleObj.optString("imagen").equals("")) {
                                //modelWarDetails.setImagen("http://www.vlim.com.mx/escudoyucatan/sin_imagen_disponible.jpg");
                            } else {
                                //Log.i(TAG, "Imagen " + i + ": " + String.valueOf(battleObj.optString("imagen")));
                                modelPublicaciones.setImagenMensaje(battleObj.optString("imagen"));
                            }

                            if (battleObj.optString("video").equals("")) {
                                //modelWarDetails.setImagen("http://www.vlim.com.mx/escudoyucatan/sin_imagen_disponible.jpg");
                            } else {
                                //Log.i(TAG, "Video " + i + ": " + String.valueOf(battleObj.optString("video")));
                                //video_pub.setVisibility(View.VISIBLE);
                                modelPublicaciones.setVideoMensaje(battleObj.optString("video"));
                            }
                            //modelWarDetails.setUsuario(battleObj.optString("id_usuario"));
                            modelPublicaciones.setImagenUsuario(battleObj.optString("imgPerfil"));
                            listPublicaciones.add(modelPublicaciones);

                        }

                        adapter = new RecyclerViewAdapter(ChatVecinalActivity.this, listPublicaciones);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No hay publicaciones", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String creatingURLConnection(String JsonDATA) {
        String response = "";
        HttpURLConnection conn;
        StringBuilder jsonResults = new StringBuilder();
        try {
            //setting URL to connect with
            URL url = new URL(Config.GET_POSTS_URL);

            //creating connection
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            //set headers and method
            Writer writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            writer.write(JsonDATA);
            // json data
            writer.close();
            /*
            converting response into String
            */
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
            response = jsonResults.toString();
            //Log.v(TAG, response);

            /*if(response.length()>0){
                Toast.makeText(getApplicationContext(), "Datos", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Sin datos", Toast.LENGTH_LONG).show();
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public void onBackPressed() { }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == NUEVA_PUBLICACION_REQUEST){
            Log.i(TAG, "back from Nueva Publicacion");
            /*finish();
            startActivity(getIntent());*/
            onRefresh();
        }
    }

}
