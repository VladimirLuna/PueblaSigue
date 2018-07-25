package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
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

public class InfoPersonalActivity extends AppCompatActivity {

    String TAG = "PUEBLA";
    String idusuario = null;
    TextView tv_titulo_toolbar, tv_direccion, tv_nombre, tv_telefono, tv_celular, tv_identificacion, tv_numero_identificacion, tv_cond_med;
    Button btn_guardar;
    EditText et_direccion, et_telefono, et_celular, et_num_identificacion, et_cond_med;
    Spinner spinner_identificacion;
    ProgressDialog progressDialog;
    JSONArray jsonArr;
    String JsonResponse = null;
    ImageView btn_back;
    ProgressDialog progressDialogIdentificaciones;
    Typeface tf;
    ArrayList tipoIdentificacionList;
    String idTipoIdentificacion[];
    String tipo_identificacion = "", direccion = "", telefono = "", celular = "", id_identificacion = "", num_identificacion = "", condicion_medica = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_info_personal);
        tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        // lee datos del usuario
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
        Log.d(TAG, "Usr: " + idusuario);

        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_direccion = findViewById(R.id.tv_direccion);
        tv_telefono = findViewById(R.id.tv_telefono);
        tv_celular = findViewById(R.id.tv_sigueme);
        tv_identificacion = findViewById(R.id.tv_identificacion);
        tv_numero_identificacion = findViewById(R.id.tv_num_identificacion);
        tv_cond_med = findViewById(R.id.tv_cond_med);
        btn_guardar = findViewById(R.id.btn_guardar);
        et_direccion = findViewById(R.id.et_direccion);
        et_telefono = findViewById(R.id.et_telefono);
        et_celular = findViewById(R.id.et_celular);
        et_num_identificacion = findViewById(R.id.et_num_identificacion);
        et_cond_med = findViewById(R.id.et_cond_med);
        spinner_identificacion = findViewById(R.id.spinner_id);
        btn_back = findViewById(R.id.btn_back);

        tv_titulo_toolbar.setTypeface(tf);
        tv_direccion.setTypeface(tf);
        tv_telefono.setTypeface(tf);
        tv_celular.setTypeface(tf);
        tv_identificacion.setTypeface(tf);
        tv_numero_identificacion.setTypeface(tf);
        tv_cond_med.setTypeface(tf);
        btn_guardar.setTypeface(tf);
        et_direccion.setTypeface(tf);
        et_telefono.setTypeface(tf);
        et_celular.setTypeface(tf);
        et_num_identificacion.setTypeface(tf);
        et_cond_med.setTypeface(tf);

        // GET CATALOGO IDENTIFICACION
        obtieneIdentificaciones();

        // GET INFO PERSONAL
        preparaID(idusuario);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        spinner_identificacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipo_identificacion = idTipoIdentificacion[position];
                Log.d(TAG, "Select identificacionid: " + tipo_identificacion + ", identificacion: " + spinner_identificacion.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String direccion = et_direccion.getText().toString();
                String telefono_casa = et_telefono.getText().toString();
                String celular = et_celular.getText().toString();
                String num_identificacion = et_num_identificacion.getText().toString();
                String condicion_med = et_cond_med.getText().toString();

                if(direccion.length() < 1){
                    et_direccion.setError("Debes escribir tu domicilio.");
                }

                else if(telefono_casa.length() < 1){
                    et_telefono.setError("Debes escribir tu número de teléfono.");
                }
                else if(celular.length() < 1){
                    et_celular.setError("Debes escribir tu número de celular.");
                }
                else if(num_identificacion.length() < 1){
                    et_num_identificacion.setError("Debes escribir tu número de identificación.");
                }
                else{
                    preparaInfo(idusuario, direccion, telefono_casa, celular, id_identificacion, num_identificacion, condicion_med);
                }
            }
        });
    }

    private void preparaInfo(String idusr, String direccion, String telefono_casa, String celular, String id_identificacion, String num_identificacion, String condicion_medica) {
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("idusr" , idusr);
            post_dict.put("direccion" , direccion);
            post_dict.put("tel", telefono_casa);
            post_dict.put("cel", celular);
            post_dict.put("id_identificacion", id_identificacion);
            post_dict.put("num_identificcion", num_identificacion);
            post_dict.put("condicion_medica", condicion_medica);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            Log.v(TAG, "postdic len: " + String.valueOf(post_dict));

            progressDialog = new ProgressDialog(InfoPersonalActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando información...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            actualizaInfo(String.valueOf(post_dict));
        }
    }

    private void actualizaInfo(String params) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String idusr = jsonObject.getString("idusr");
            String direccion = jsonObject.getString("direccion");
            String tel = jsonObject.getString("tel");
            String cel = jsonObject.getString("cel");
            String id_identificacion = jsonObject.getString("id_identificacion");
            String num_identificacion = jsonObject.getString("num_identificcion");
            String condicion_medica = jsonObject.getString("condicion_medica");

            jsonBody.put("idusr", idusr);
            jsonBody.put("direccion", direccion);
            jsonBody.put("tel", tel);
            jsonBody.put("cel", cel);
            jsonBody.put("id_identificacion", id_identificacion);
            jsonBody.put("num_identificcion", num_identificacion);
            jsonBody.put("condicion_medica", condicion_medica);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UPDATE_INFO_USUARIO, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "VOLLEY response update info: " + response);
                    progressDialog.dismiss();
                    showAlert("Tu información  personal ha sido actualizada.");
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

    private void obtieneIdentificaciones() {
        progressDialogIdentificaciones = new ProgressDialog(InfoPersonalActivity.this);
        progressDialogIdentificaciones.setCancelable(false);
        progressDialogIdentificaciones.setMessage("Obteniendo información...");
        progressDialogIdentificaciones.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialogIdentificaciones.setProgress(0);
        progressDialogIdentificaciones.show();

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
                Log.i(TAG, "VOLLEY response submotivos: " + response);

                tipoIdentificacionList = new ArrayList();
                idTipoIdentificacion = new String[10];

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

                        idTipoIdentificacion[i] = id_identificacion;
                        tipoIdentificacionList.add(tipo_identificacion);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }// for

                progressDialogIdentificaciones.dismiss();
                //@SuppressWarnings("unchecked")
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(InfoPersonalActivity.this, android.R.layout.simple_spinner_dropdown_item, tipoIdentificacionList){
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
                spinner_identificacion.setAdapter(adapter1);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "VOLLEY: " + error.toString());
                progressDialogIdentificaciones.dismiss();
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

            progressDialog = new ProgressDialog(InfoPersonalActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Leyendo contactos, por favor espere...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            obtieneInfoPersonal(String.valueOf(post_dict));
        }
    }

    private void obtieneInfoPersonal(String params) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String usuario = jsonObject.getString("idusr");

            jsonBody.put("idusr", usuario);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_INFO_USUARIO, new Response.Listener<String>() {
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
                            direccion = jsonObj.getString("direccion");
                            telefono = jsonObj.getString("telefono_casa");
                            celular = jsonObj.getString("celular");
                            id_identificacion = jsonObj.getString("id_identificacion");
                            num_identificacion = jsonObj.getString("num_identificcion");
                            condicion_medica = jsonObj.getString("condicion_medica");
                            Log.i(TAG, "Recupera info OK");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    et_direccion.setText(direccion);
                    et_telefono.setText(telefono);
                    et_celular.setText(celular);
                    et_num_identificacion.setText(num_identificacion);
                    et_cond_med.setText(condicion_medica);

                    Log.d(TAG, "Intento de cambiar el spinner...");
                    spinner_identificacion.setSelection(Integer.parseInt(id_identificacion) - 1);
                    //selectValue(spinner_identificacion, Long.parseLong(id_identificacion));
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

    private void selectValue(Spinner spinner, long  value) {
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) spinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if(adapter.getItemId(position) == value) {
                spinner.setSelection(position);
                return;
            }
        }
    }

    @Override
    public void onBackPressed() { }

    private void showAlert(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Información personal")
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
}
