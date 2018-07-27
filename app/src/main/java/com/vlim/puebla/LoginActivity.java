package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    EditText et_usuario, et_password;
    TextView tv_usuario, tv_pass, tv_olvidepass, tv_registro;
    String TAG = "PUEBLA";
    String response = null;
    String usr  = null, pass = null;
    JSONArray jsonArr;
    String JsonResponse = null;
    Integer puedeRegistrar = 0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        tv_usuario = findViewById(R.id.tv_celular);
        tv_pass = findViewById(R.id.tv_pass);
        tv_olvidepass = findViewById(R.id.tv_olvidepass);
        et_usuario = findViewById(R.id.et_usuario);
        et_password = findViewById(R.id.et_passusuario);
        btn_login = findViewById(R.id.btn_login);
        tv_registro = findViewById(R.id.tv_registrar);
        tv_usuario.setTypeface(tf);
        tv_pass.setTypeface(tf);
        tv_olvidepass.setTypeface(tf);
        et_usuario.setTypeface(tf);
        et_password.setTypeface(tf);
        btn_login.setTypeface(tf);
        tv_registro.setTypeface(tf);

        // lee datos del usuario
        userSQLiteHelper dbLogin =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = dbLogin.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT usr, password FROM Usuarios", null);
        String password = null;
        if (c.moveToFirst()) {
            Log.v(TAG, "hay cosas");
            String usr_recover = c.getString(0);
            password = c.getString(1);

            et_usuario.setText(usr_recover.toString());
            c.close();
            db.close();
            preparaLogin(usr_recover, password);


            Log.v(TAG, usr_recover);
        }
        else{
            Log.v(TAG, "NO hay cosas");
        }


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usr = et_usuario.getText().toString().trim();
                pass = et_password.getText().toString().trim();
                if(usr.equalsIgnoreCase("")){
                    et_usuario.setError("Ingresa tu nombre de usuario");
                }
                else if(pass.equalsIgnoreCase("")){
                    et_password.setError("Ingresa tu contraseña");
                }
                else{
                    preparaLogin(usr, pass);
                }
            }
        });

        tv_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registro = new Intent(LoginActivity.this, RegistroUsuarioActivity.class);
                startActivity(registro);
            }
        });

        tv_olvidepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent recuperaPass = new Intent(LoginActivity.this, RecuperaPasswordActivity.class);
                startActivity(recuperaPass);
            }
        });
    }

    private void preparaLogin(String usr, String pass) {
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("usuario" , usr);
            post_dict.put("password", pass);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            Log.v(TAG, "postdic len: " + String.valueOf(post_dict));

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Validando...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            login(String.valueOf(post_dict));
        }
    }

    private void login(String params) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String usuario = jsonObject.getString("usuario");
            String password = jsonObject.getString("password");
            jsonBody.put("usuario", usuario);
            jsonBody.put("password", password);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.LOGIN_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.i("VOLLEYresponse", String.valueOf(jsonArr));
                    Log.i(TAG, "VOLLEY response login: " + response);
                    progressDialog.dismiss();
                    entrar(response);

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

    private void entrar(String response) {
        Log.i(TAG, "Respuesta entrar: " + response);
        String nick = "", respuesta = "", nombreUsuario = "", idusuario = "", imagenusr = "";
        try {
            JSONArray array = new JSONArray(response);
            JSONObject obj;
            for (int i = 0; i < array.length(); i++) {
                obj = array.getJSONObject(i);
                respuesta = obj.getString("respuesta");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        //Toast.makeText(getApplicationContext(), respuesta + ", " , Toast.LENGTH_LONG).show();
        Log.d(TAG, "Respuesta: " + respuesta);
        if(respuesta.equals("OK")){
            try {
                JSONArray arrayOK = new JSONArray(response);
                JSONObject obj;
                for (int i = 0; i < arrayOK.length(); i++) {
                    obj = arrayOK.getJSONObject(i);
                    nick = obj.getString("nick");
                    nombreUsuario = obj.getString("nombreUsuario");
                    idusuario = obj.getString("idusuario");
                    imagenusr = obj.getString("img");
                    puedeRegistrar = obj.getInt("isReg");
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            // Guarda en BD
            //Abrimos la base de datos 'DBUsuarios' en modo escritura
            userSQLiteHelper usdbh =
                    new userSQLiteHelper(LoginActivity.this, "DBUsuarios", null, Config.VERSION_DB);

            SQLiteDatabase db = usdbh.getWritableDatabase();

            //Si hemos abierto correctamente la base de datos
            if (db != null) {
                //Log.v(TAG, "Hay base login " + imagenusr);
                //Insertamos los datos en la tabla Usuarios
                db.execSQL("INSERT INTO Usuarios (idusuario, nick, nombre, usr, password, img) " +
                        "VALUES (" + idusuario + ", '" + nick + "', '" + nombreUsuario + "', '" + usr + "', '" + pass +"', '" + imagenusr +"')");

                //Cerramos la base de datos
                db.close();
            } else {
                Log.v(TAG, "No Hay base");
            }

            Intent entrar = new Intent(LoginActivity.this, MainActivity.class);
            Bundle datosUsuario = new Bundle();
            datosUsuario.putInt("isReg", puedeRegistrar);
            entrar.putExtras(datosUsuario);
            startActivity(entrar);
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
            borraBD();
        }
    }

    private void borraBD() {
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        db.delete("Usuarios", null, null);
        Log.i(TAG, "Elimina base Usuarios");
    }


}

/*
* String submotivo = request.getParameter("idsubmotivo").toString();
        String idusr = request.getParameter("idusr").toString();
        String lat = request.getParameter("lat").toString();
        String lon = request.getParameter("long").toString();
        String descripcion = request.getParameter("descripcion").toString();


        png/mp4/mp3
* */
