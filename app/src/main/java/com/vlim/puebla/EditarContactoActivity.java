package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditarContactoActivity extends AppCompatActivity {

    String TAG = "PUEBLA";
    TextView tv_titulo_toolbar, tv_nombre, tv_telefono, tv_celular, tv_correo, tv_mensaje;
    EditText et_nombre, et_telefono, et_celular, et_correo;
    ImageView btn_back;
    Button btn_actualizar;
    String id_contacto;
    ProgressDialog progressDialog;
    JSONArray jsonArr;
    String JsonResponse = null;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contacto);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        // Toolbar
        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        btn_back = findViewById(R.id.btn_back);
        tv_nombre = findViewById(R.id.tv_motivo);
        tv_telefono = findViewById(R.id.tv_telefono);
        tv_celular = findViewById(R.id.tv_celular);
        tv_correo = findViewById(R.id.tv_mail);
        et_nombre = findViewById(R.id.et_nombre);
        et_telefono = findViewById(R.id.et_telefono);
        et_celular = findViewById(R.id.et_celular);
        et_correo = findViewById(R.id.et_mail);
        btn_actualizar = findViewById(R.id.btn_actualizar);
        tv_mensaje = findViewById(R.id.tv_mensaje);

        tv_titulo_toolbar.setTypeface(tf);
        tv_nombre.setTypeface(tf);
        tv_telefono.setTypeface(tf);
        tv_celular.setTypeface(tf);
        tv_correo.setTypeface(tf);
        et_nombre.setTypeface(tf);
        et_telefono.setTypeface(tf);
        et_celular.setTypeface(tf);
        et_correo.setTypeface(tf);
        btn_actualizar.setTypeface(tf);
        tv_mensaje.setTypeface(tf);

        Intent i = getIntent();
        id_contacto = i.getStringExtra("id_contacto");

        obtieneDetalleContacto(id_contacto);

        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = et_nombre.getText().toString();
                String telefono = et_telefono.getText().toString();
                String celular = et_celular.getText().toString();
                String correo = et_correo.getText().toString();

                if(nombre.length() < 1){
                    et_nombre.setError("Escribe el nombre de tu contacto");
                }
                /*else if(telefono.length() < 1){
                    et_telefono.setError("Escribe el teléfono de tu contacto");
                }*/
                else if(celular.length() < 1){
                    et_celular.setError("Escribe el celular de tu contacto");
                }
                else if(correo.length() < 1){
                    et_correo.setError("Escribe el correo electrónico de tu contacto");
                }
                /*else{
                    // Envia parámetros
                    preparaContacto(id_contacto, nombre, telefono, celular, correo);
                }*/
                else{
                    if(!validaNombre(nombre)){
                        Log.d(TAG, "Error, nombre con caracteres no válidos");
                        et_nombre.setError("Nombre incorrecto");
                    }
                    else{
                        Log.i(TAG, "Datos correctos");
                        if(validaTelefono(telefono)){
                            Log.d(TAG, "Telefono valido");
                            if(validaTelefono(celular)){
                                Log.d(TAG, "Celular valido");

                                if(!isEmailValid(correo)){
                                    et_correo.setError("Ingresa un correo electrónico válido.");
                                }
                                else{
                                    // Envia parámetros
                                    preparaContacto(id_contacto, nombre, telefono, celular, correo);
                                }
                            }
                            else{
                                et_celular.setError("Teléfono celular no válido");
                            }
                        }
                        else{
                            et_telefono.setError("Teléfono no válido");
                        }
                    }
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

    public static boolean validaTelefono( String tel )
    {
        String regx = "^[0-9]{5,10}$";

        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(tel);
        return matcher.find();
    }

    public static boolean validaNombre( String nombre )
    {
        String regx = "^[a-zA-Z\\sáéíóúñüàèñ]{3,35}$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(nombre);
        return matcher.find();
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void preparaContacto(String idcontacto, String nombre, String tel, String cel, String correo) {
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("idcontacto" , idcontacto);
            post_dict.put("nombre", nombre);
            post_dict.put("tel", tel);
            post_dict.put("cel", cel);
            post_dict.put("correo", correo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            Log.v(TAG, "postdic len: " + String.valueOf(post_dict));

            progressDialog = new ProgressDialog(EditarContactoActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando contacto...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            actualizaContacto(String.valueOf(post_dict));
        }
    }

    private void actualizaContacto(String params) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String idcontacto = jsonObject.getString("idcontacto");
            String nombre = jsonObject.getString("nombre");
            String tel = jsonObject.getString("tel");
            String cel = jsonObject.getString("cel");
            String correo = jsonObject.getString("correo");

            jsonBody.put("idcontacto", idcontacto);
            jsonBody.put("nombre", nombre);
            jsonBody.put("tel", tel);
            jsonBody.put("cel", cel);
            jsonBody.put("correo", correo);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UPDATE_CONTACTO_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "VOLLEY response registro contacto: " + response);
                    progressDialog.dismiss();

                    String respuesta="", mensaje="";
                    try {
                        JSONArray array = new JSONArray(response);
                        JSONObject obj;
                        for (int i = 0; i < array.length(); i++) {
                            obj = array.getJSONObject(i);
                            respuesta = obj.getString("respuesta");
                            mensaje = obj.getString("mensaje");
                        }

                        if(respuesta.equals("OK")){
                            showAlert(mensaje);
                        }else{
                            showAlert(respuesta + ": " + mensaje);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

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

    private void showAlert(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Contactos de emergencia")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // ok
                        finish();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    private void obtieneDetalleContacto(String id_contacto) {
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Leyendo información...");
        pDialog.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            jsonBody.put("idusr", id_contacto);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_INFO_CONTACTO_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                        Log.i(TAG, "response: " + jsonArr);

                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObj = null;
                            try {
                                jsonObj = jsonArr.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String nombre_completo = jsonObj.getString("nombre_completo");
                            String telefono = jsonObj.getString("telefono");
                            String celular = jsonObj.getString("celular");
                            String correo_contacto = jsonObj.getString("correo_contacto");

                            Log.i(TAG, "info contacto nombre: " + nombre_completo + ", tel: " + telefono);

                            et_nombre.setText(nombre_completo);
                            et_telefono.setText(telefono);
                            et_celular.setText(celular);
                            et_correo.setText(correo_contacto);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pDialog.dismiss();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "VOLLEY: " + error.toString());
                    ////showProgress(false);
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error en la comunicación.", Toast.LENGTH_LONG).show();
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
}
