package com.vlim.puebla;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class RegistroUsuarioActivity2 extends AppCompatActivity {

    // Toolbar
    TextView tv_titulo_toolbar, tv_mensaje, tv_correo, tv_pass, tv_tipoid, tv_numid, tv_colonia, tv_padecimientos;
    ImageView btn_back;
    EditText et_correo, et_pass, et_numidentificacion, et_padecimientos;
    Spinner spinner_tipodocumento, spinner_colonia;
    String TAG = "PUEBLA";
    Typeface tf;
    String tipo_identificacion = "", colonia = "", idusuario = "";
    String idSubmotivos[], idColonia[];
    JSONArray jsonArr;
    String JsonResponse = null;
    ArrayList identificacionlist, colonialist;
    ImageButton btn_siguiente;
    String nombrecompleto, domicilio, telefono, celular;
    NetworkConnection nt_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario2);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        Intent i= getIntent();
        nombrecompleto = i.getStringExtra("nombrecompleto");
        domicilio = i.getStringExtra("domicilio");
        telefono = i.getStringExtra("telefono");
        celular = i.getStringExtra("celular");

        // Toolbar
        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = findViewById(R.id.btn_back);

        tv_mensaje = findViewById(R.id.tv_mensaje);
        tv_mensaje.setTypeface(tf);
        tv_correo = findViewById(R.id.tv_correo);
        tv_correo.setTypeface(tf);
        tv_pass = findViewById(R.id.tv_pass);
        tv_pass.setTypeface(tf);
        tv_tipoid = findViewById(R.id.tv_identificacion);
        tv_tipoid.setTypeface(tf);
        tv_numid = findViewById(R.id.tv_numidentificacion);
        tv_numid.setTypeface(tf);
        tv_colonia = findViewById(R.id.tv_colonia);
        tv_colonia.setTypeface(tf);
        tv_padecimientos = findViewById(R.id.tv_padecimientos);
        tv_padecimientos.setTypeface(tf);

        et_correo = findViewById(R.id.et_correo);
        et_correo.setTypeface(tf);
        et_pass = findViewById(R.id.et_password);
        et_pass.setTypeface(tf);
        et_numidentificacion = findViewById(R.id.et_numidentificacion);
        et_numidentificacion.setTypeface(tf);
        et_padecimientos = findViewById(R.id.et_padecimientos);
        et_padecimientos.setTypeface(tf);
        spinner_tipodocumento = findViewById(R.id.spinner_tipoid);
        spinner_colonia = findViewById(R.id.spinner_colonia);
        btn_siguiente = findViewById(R.id.btn_siguiente);

        spinner_tipodocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipo_identificacion = idSubmotivos[position];
                Log.d(TAG, "Select identificacionid: " + tipo_identificacion + ", identificacion: " + spinner_tipodocumento.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_colonia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                colonia = idColonia[position];
                Log.d(TAG, "Select colonia: " + colonia + ", col: " + spinner_colonia.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        obtieneTipoDocumento();
        obtieneColonias();

        btn_siguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    String correo = et_correo.getText().toString().trim();
                    String pass = et_pass.getText().toString().trim();
                    String numid = et_numidentificacion.getText().toString().trim();
                    String padecimientos = et_padecimientos.getText().toString().trim();

                    if(correo.length() < 1){
                        et_correo.setError("Ingresa el correo electrónico.");
                    }
                    else if(pass.length() < 1){
                        et_pass.setError("Ingresa la contraseña.");
                    }
                    else if(numid.length() < 1){
                        et_numidentificacion.setError("Ingresa el número de identificación.");
                    }
                    else{
                        // check num identificacion
                        if(tipo_identificacion.equals("1") || tipo_identificacion.equals("2")){
                            if(et_numidentificacion.length() < 19 && et_numidentificacion.length() > 10){
                                flag = 1;
                            }
                            else{
                                et_numidentificacion.setError("Ingrese el número de identificación correcto");
                                flag = 0;
                            }
                        }
                        else{
                            if(et_numidentificacion.length() < 31 && et_numidentificacion.length() > 8){
                                flag = 1;
                            }
                            else{
                                et_numidentificacion.setError("Ingrese el número de identificación correcto");
                                flag = 0;
                            }
                        }

                        if(flag == 1){
                            Log.i(TAG, "Envio OK");
                            Intent datosCuentaIntent = new Intent(RegistroUsuarioActivity2.this, RegistroUsuarioActivity3.class);
                            datosCuentaIntent.putExtra("nombrecompleto", nombrecompleto);
                            datosCuentaIntent.putExtra("domicilio", domicilio);
                            datosCuentaIntent.putExtra("telefono", telefono);
                            datosCuentaIntent.putExtra("celular", celular);
                            // 2
                            datosCuentaIntent.putExtra("correo", correo);
                            datosCuentaIntent.putExtra("pass", pass);
                            datosCuentaIntent.putExtra("tipo_identificacion", tipo_identificacion);
                            datosCuentaIntent.putExtra("numid", numid);
                            datosCuentaIntent.putExtra("colonia", colonia);
                            datosCuentaIntent.putExtra("padecimientos", padecimientos);

                            Log.i(TAG, "Reg2 tipoid: " + tipo_identificacion + ", numeroid: " + numid + ", correo: " + correo + ", padecimientos: " + padecimientos);
                            startActivity(datosCuentaIntent);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Por favor revise los datos ingresados.", Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Se requiere conexión a Internet.", Toast.LENGTH_LONG).show();
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

    private void obtieneTipoDocumento() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final JSONObject jsonBody = new JSONObject();
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.GET_TIPO_DOCUMENTO_URL, new Response.Listener<String>() {
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


                identificacionlist = new ArrayList();
                idSubmotivos = new String[jsonArr.length()];

                /* Muestra JSON array */
                for (int i = 0; i < jsonArr.length(); i++){
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = jsonArr.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        String tipo_identificacion = jsonObj.getString("tipo_identificacion");
                        String id_identificacion = jsonObj.getString("id_identificacion");
                        System.out.println(id_identificacion + ", " + tipo_identificacion);

                        idSubmotivos[i] = id_identificacion;
                        identificacionlist.add(tipo_identificacion);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }// for
                //spinner_submotivos.setAdapter(new ArrayAdapter<String>(DetalleEmergenciaActivity.this, android.R.layout.simple_spinner_dropdown_item, motivoslist));
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(RegistroUsuarioActivity2.this, android.R.layout.simple_spinner_dropdown_item, identificacionlist){
                    public View getView(int position, View convertView, android.view.ViewGroup parent) {
                        TextView v = (TextView) super.getView(position, convertView, parent);
                        v.setTypeface(tf);
                        return v;
                    }
                    public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                        TextView v = (TextView) super.getView(position, convertView, parent);
                        v.setTypeface(tf);
                        return v;
                    }
                };
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_tipodocumento.setAdapter(adapter1);

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
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
    }

    private void obtieneColonias() {

        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        //Cursor c = db.query("Usuarios", campos, "idusuario=?", args, null, null, null);
        Cursor c = db.rawQuery("SELECT idusuario, nick, img FROM Usuarios", null);
        Log.i(TAG, "cols: " + c.getCount());

        if (c.moveToFirst()) {

            Log.v(TAG, "hay cosas settings");
            idusuario = c.getString(0);
        }
        else{
            Log.v(TAG, "NO hay cosas settings");
        }

        db.close();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("idusr", idusuario);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_CAT_COLONIA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    jsonArr = new JSONArray(response);
                    JsonResponse = response;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "VOLLEYresponse colonias: " + response);

                colonialist = new ArrayList();
                idColonia = new String[jsonArr.length()];

                /* Muestra JSON array */
                for (int i = 0; i < jsonArr.length(); i++){
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = jsonArr.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        String nombre = jsonObj.getString("nombre");
                        String id_cat_colonia = jsonObj.getString("id_cat_colonia");
                        System.out.println(id_cat_colonia + ", " + nombre);

                        idColonia[i] = id_cat_colonia;
                        colonialist.add(nombre);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }// for
                //spinner_submotivos.setAdapter(new ArrayAdapter<String>(DetalleEmergenciaActivity.this, android.R.layout.simple_spinner_dropdown_item, motivoslist));
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(RegistroUsuarioActivity2.this, android.R.layout.simple_spinner_dropdown_item, colonialist){
                    public View getView(int position, View convertView, android.view.ViewGroup parent) {
                        TextView v = (TextView) super.getView(position, convertView, parent);
                        v.setTypeface(tf);
                        return v;
                    }
                    public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                        TextView v = (TextView) super.getView(position, convertView, parent);
                        v.setTypeface(tf);
                        return v;
                    }
                };
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner_colonia.setAdapter(adapter1);

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY", error.toString());
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
    }

    @Override
    public void onBackPressed() { }

}
