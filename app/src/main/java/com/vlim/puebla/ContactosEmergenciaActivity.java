package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.List;

public class ContactosEmergenciaActivity extends AppCompatActivity implements OnCustomClickListener{

    ImageView btn_back;
    String TAG = "PUEBLA";
    String idusuario = null;
    ProgressDialog progressDialog;
    JSONArray jsonArr;
    String JsonResponse = null;
    Contactos contactos;
    private List<Contactos> contactosList = new ArrayList<Contactos>();
    private ListView listView;
    private CustomContactosListAdapter customAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos_emergencia);

        // lee datos del usuario
        String[] campos = new String[] {"idusuario", "nick", "nombre"};

        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT idusuario, nick, nombre FROM Usuarios", null);

        if (c.moveToFirst()) {
            Log.v(TAG, "hay cosas");
            //Recorremos el cursor hasta que no haya más registros
            do {
                idusuario = c.getString(0);
            } while(c.moveToNext());
        }
        else{
            Log.v(TAG, "NO hay cosas");
        }
        db.close();
        //////

        listView = findViewById(R.id.list_contactos);
        customAdapter = new CustomContactosListAdapter(ContactosEmergenciaActivity.this, contactosList, this);
        listView.setAdapter(customAdapter);

        preparaID(idusuario);

        btn_back = findViewById(R.id.btn_back);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void preparaID(String idusuario) {
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("idusr" , idusuario);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            Log.v(TAG, "postdic len: " + String.valueOf(post_dict));

            progressDialog = new ProgressDialog(ContactosEmergenciaActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Leyendo contactos, por favor espere...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            recuperaContactos(String.valueOf(post_dict));
        }
    }

    private void recuperaContactos(String params) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String usuario = jsonObject.getString("idusr");

            jsonBody.put("idusr", usuario);

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

                            contactos = new Contactos();
                            contactos.setIdContacto(id_usuario_contacto);
                            contactos.setNombreContacto(nombre_completo);

                            contactosList.add(contactos);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    customAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
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

    @Override
    public void onBackPressed() { }

    @Override
    public void OnCustomClick(View aView, int position) {
        Log.i(TAG, "contacto sel: " + contactosList.get(position).getIdContacto() + " - " + contactosList.get(position).getNombreContacto());
        String idContacto = contactosList.get(position).getIdContacto();
        String nombreContacto = contactosList.get(position).getNombreContacto();
        Intent detalleContactoIntent = new Intent(ContactosEmergenciaActivity.this, DetalleContactoActivity.class);
        detalleContactoIntent.putExtra("id_grupo", idContacto);
        detalleContactoIntent.putExtra("nombre_grupo", nombreContacto);
        ///finish();
        startActivity(detalleContactoIntent);
    }

}
