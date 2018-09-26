package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class AgregaContactoEmergenciaActivity extends AppCompatActivity {

    String TAG = "PUEBLA";
    TextView tv_titulo_toolbar, tv_nombre, tv_telefono, tv_celular, tv_correo, tv_mensaje;
    EditText et_nombre, et_telefono, et_celular, et_correo;
    ImageView btn_back;
    Button btn_guardar;
    String idusuario, correo_usr;
    ProgressDialog progressDialog;
    JSONArray jsonArr;
    String JsonResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agrega_contacto_emergencia);

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
        btn_guardar = findViewById(R.id.btn_guardar);
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
        btn_guardar.setTypeface(tf);
        tv_mensaje.setTypeface(tf);

        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT idusuario, usr FROM Usuarios", null);

        if (c.moveToFirst()) {
            Log.v(TAG, "hay cosas");
            //Recorremos el cursor hasta que no haya más registros
            do {
                idusuario = c.getString(0);
                correo_usr = c.getString(1);
                Log.d(TAG, "correo_usr: " + correo_usr);
            } while(c.moveToNext());
        }
        else{
            Log.v(TAG, "NO hay cosas");
        }
        c.close();
        db.close();
        //////

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = et_nombre.getText().toString();
                String telefono = et_telefono.getText().toString();
                String celular = et_celular.getText().toString();
                String correo = et_correo.getText().toString();
                int tagValido = 0;

                if(nombre.length() < 1){
                    et_nombre.setError("Escribe el nombre de tu contacto");
                    tagValido = 0;
                }
                /*else if(telefono.length() < 1){
                    et_telefono.setError("Escribe el teléfono de tu contacto");
                }*/
                else if(celular.length() < 1){
                    et_celular.setError("Escribe el celular de tu contacto");
                    tagValido = 0;
                }
                else if(correo.length() < 1){
                    et_correo.setError("Escribe el correo electrónico de tu contacto");
                    tagValido = 0;
                }
                else if(!isEmailValid(correo)){
                    et_correo.setError("Ingresa un correo electrónico válido.");
                    tagValido = 0;
                }
                else if(correo.equals(correo_usr)){
                    et_correo.setError("El correo electrónico de tu contacto de emergencia debe ser diferentes a tu correo electrónico.");
                    tagValido = 0;
                }
                else
                    tagValido = 1;

                if(tagValido == 1){
                    // Envia parámetros
                    Log.d(TAG, "envia parametros");
                    registraContacto(idusuario, nombre, telefono, celular, correo);
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

    /*private void registraContacto(String idusr, String nombre, String tel, String cel, String correo) {
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("idusr" , idusr);
            post_dict.put("nombre", nombre);
            post_dict.put("tel", tel);
            post_dict.put("cel", cel);
            post_dict.put("correo", correo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            Log.v(TAG, "postdic len: " + String.valueOf(post_dict));

            registraContacto(String.valueOf(post_dict));
        }
    }*/

    private void registraContacto(String idusr, String nombre, String tel, String cel, String correo) {
        try {
            progressDialog = new ProgressDialog(AgregaContactoEmergenciaActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Registrando contacto...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            /*JSONObject jsonObject = new JSONObject(params);
            String idusr = jsonObject.getString("idusr");
            String nombre = jsonObject.getString("nombre");
            String tel = jsonObject.getString("tel");
            String cel = jsonObject.getString("cel");
            String correo = jsonObject.getString("correo");*/

            jsonBody.put("idusr", idusr);
            jsonBody.put("nombre", nombre);
            jsonBody.put("tel", tel);
            jsonBody.put("cel", cel);
            jsonBody.put("correo", correo);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.INSERT_CONTACTO_URL, new Response.Listener<String>() {
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

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
