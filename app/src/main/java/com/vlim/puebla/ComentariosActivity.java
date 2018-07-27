package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    String idPublicacion = null;
    ProgressDialog progressDialog;
    JSONArray jsonArr;
    String JsonResponse = null;
    private RecyclerView mRVFishPrice;
    private ComentariosAdapter mAdapter;
    Calendar calendar = Calendar.getInstance();
    DateFormat formatter = new SimpleDateFormat("dd/MMMM/yyyy");
    TextView tv_nuevapublicacion;
    EditText et_comentarionuevo;
    String respuesta = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    // Toolbar
    TextView tv_titulo_toolbar;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        // Toolbar
        tv_titulo_toolbar = (TextView) findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = (ImageView) findViewById(R.id.btn_back);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                idPublicacion = null;
            } else {
                idPublicacion = extras.getString("idPublicacion");
                //tv_comentarios.setText("ID publicacion: " + idPublicacion);
                obtieneComentarios(idPublicacion);
                progressDialog = new ProgressDialog(ComentariosActivity.this);
                progressDialog.setMessage("Recuperando comentarios...");
                progressDialog.setIndeterminate(false);
                progressDialog.show();
            }
        }else {
            idPublicacion = (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }

        et_comentarionuevo = (EditText) findViewById(R.id.et_comentario);
        et_comentarionuevo.setTypeface(tf);
        tv_nuevapublicacion = (TextView) findViewById(R.id.btn_enviarreporte);
        tv_nuevapublicacion.setTypeface(tf);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(ComentariosActivity.this);


        tv_nuevapublicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nuevoComentario = et_comentarionuevo.getText().toString().trim();
                if(nuevoComentario.length() > 0){
                    publicaNuevoComentario(idPublicacion, nuevoComentario, idUsr());
                    progressDialog = new ProgressDialog(ComentariosActivity.this);
                    progressDialog.setMessage("Guardando comentario...");
                    progressDialog.setIndeterminate(false);
                    progressDialog.show();
                }else{
                    et_comentarionuevo.setError("Escribe un comentario");
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String idUsr() {
        String idUsuario = null;
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT idusuario FROM Usuarios", null);

        if (c.moveToFirst()) {
            Log.v("sqlidusr", "hay cosas");
            idUsuario = c.getString(0);

            Log.v("sqlidusr", idUsuario);
        }
        else{
            Log.v("sqlidusr", "NO hay cosas");
        }
        c.close();
        db.close();
        return idUsuario;
    }

    private void publicaNuevoComentario(String idPublicacion, String nuevoComentario, String idUsr) {
        Log.d("nuevocoment", idUsr);
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();
            // idPost, comentario, idusr
            jsonBody.put("idPost", idPublicacion);
            jsonBody.put("comentario", nuevoComentario);
            jsonBody.put("idusr", idUsr);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.AGREGA_COMENTARIOS_URL, new Response.Listener<String>() {
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
                    Log.i("VOLLEYresponse", response);


                    /* Muestra JSON array */
                    for (int i = 0; i < jsonArr.length(); i++)
                    {
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = jsonArr.getJSONObject(i);
                            DataComentarios comentariosData = new DataComentarios();
                            respuesta = jsonObj.getString("respuesta");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    if(respuesta.equals("OK")){
                        Toast.makeText(getApplicationContext(), "Se ha registrado tu comentario.", Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(getIntent());
                    }
                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error en la conexión, intente de nuevo.", Toast.LENGTH_LONG).show();
                    Log.e("VOLLEY", error.toString());
                    NetworkResponse errorRes = error.networkResponse;
                    String stringData = "";
                    if(errorRes != null && errorRes.data != null){
                        try {
                            stringData = new String(errorRes.data,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e("Error",stringData);
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
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void obtieneComentarios(String idPost) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("idPost", idPost);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_COMENTARIOS_URL, new Response.Listener<String>() {
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
                    Log.i("VOLLEYresponse", response);

                    /* [{"id_comentario":80,
                    "comentario":"Dime como subes la imagen por fa ",
                    "id_usuario":82,
                    "id_publicaciones":84,
                    "activo":true,
                    "fecha_publicacion":1504587600000, fecha en milis
                    "nickname":"veronica"}]*/

                    List<DataComentarios> data = new ArrayList<>();
                    /* Muestra JSON array */
                    for (int i = 0; i < jsonArr.length(); i++)
                    {
                        JSONObject jsonObj = null;
                        try {
                            jsonObj = jsonArr.getJSONObject(i);
                            DataComentarios comentariosData = new DataComentarios();
                            comentariosData.nickUsuario = jsonObj.getString("nickname");
                            comentariosData.comentario = jsonObj.getString("comentario");

                            calendar.setTimeInMillis(Long.parseLong(jsonObj.getString("fecha_publicacion")));
                                /*int mYear = calendar.get(Calendar.YEAR);
                                int mMonth = calendar.get(Calendar.MONTH);
                                int mDay = calendar.get(Calendar.DAY_OF_MONTH);*/

                            comentariosData.fecha = formatter.format(calendar.getTime());
                            data.add(comentariosData);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                            /*try {
                                String comentario = jsonObj.getString("comentario");
                                System.out.println(comentario);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                    }
                    // Setup and Handover data to recyclerview
                    mRVFishPrice = (RecyclerView)findViewById(R.id.comentariosGeneralRecycler);
                    mAdapter = new ComentariosAdapter(ComentariosActivity.this, data);
                    mRVFishPrice.setAdapter(mAdapter);
                    mRVFishPrice.setLayoutManager(new LinearLayoutManager(ComentariosActivity.this));
                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error en la conexión, intente de nuevo.", Toast.LENGTH_LONG).show();
                    Log.e("VOLLEY", error.toString());
                    NetworkResponse errorRes = error.networkResponse;
                    String stringData = "";
                    if(errorRes != null && errorRes.data != null){
                        try {
                            stringData = new String(errorRes.data,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e("Error",stringData);
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
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        Toast.makeText(getApplicationContext(), "Lista de comentarios actualizada", Toast.LENGTH_LONG).show();
        startActivity(getIntent());
        finish();
    }

    @Override
    public void onBackPressed() { }
}
