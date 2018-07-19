package com.vlim.puebla;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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
    TextView tv_titulo_toolbar, tv_mensaje, tv_usuario, tv_pass, tv_tipoid, tv_numid;
    ImageView btn_back;
    EditText et_usuario, et_pass, et_numidentificacion;
    Spinner spinner_tipodocumento;
    String TAG = "PUEBLA";
    Typeface tf;
    String tipo_identificacion = "";
    String idSubmotivos[];
    JSONArray jsonArr;
    String JsonResponse = null;
    ArrayList identificacionlist;
    ImageButton btn_contactoemergencia;
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
        tv_titulo_toolbar = (TextView) findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = (ImageView) findViewById(R.id.btn_back);

        tv_mensaje = (TextView) findViewById(R.id.tv_mensaje);
        tv_mensaje.setTypeface(tf);
        tv_usuario = (TextView) findViewById(R.id.tv_nombrecompleto);
        tv_usuario.setTypeface(tf);
        tv_pass = (TextView) findViewById(R.id.tv_tel);
        tv_pass.setTypeface(tf);
        tv_tipoid = (TextView) findViewById(R.id.tv_cel);
        tv_tipoid.setTypeface(tf);
        tv_numid = (TextView) findViewById(R.id.tv_usuario);
        tv_numid.setTypeface(tf);
        et_usuario = (EditText) findViewById(R.id.et_nombrecontacto);
        et_usuario.setTypeface(tf);
        et_pass = (EditText) findViewById(R.id.et_telcontacto);
        et_pass.setTypeface(tf);
        et_numidentificacion = (EditText) findViewById(R.id.et_celcontacto);
        et_numidentificacion.setTypeface(tf);
        spinner_tipodocumento = (Spinner) findViewById(R.id.spinner_tipoid);
        btn_contactoemergencia = (ImageButton) findViewById(R.id.btn_enviardenuncia);

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

        obtieneTipoDocumento();

        btn_contactoemergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 0;
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    String usuario = et_usuario.getText().toString().trim();
                    String pass = et_pass.getText().toString().trim();
                    String numid = et_numidentificacion.getText().toString().trim();

                    if(usuario.length() < 1){
                        et_usuario.setError("Ingresa el usuario.");
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
                            datosCuentaIntent.putExtra("usuario", usuario);
                            datosCuentaIntent.putExtra("pass", pass);
                            datosCuentaIntent.putExtra("tipo_identificacion", tipo_identificacion);
                            datosCuentaIntent.putExtra("numid", numid);

                            Log.i(TAG, "Reg2 tipoid: " + tipo_identificacion + ", numeroid: " + numid);
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

            /*JSONObject jsonObject = new JSONObject(params);
            String institucion = jsonObject.getString("institucion");
            jsonBody.put("institucion", institucion);*/

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
                idSubmotivos = new String[10];

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

    @Override
    public void onBackPressed() { }

}
