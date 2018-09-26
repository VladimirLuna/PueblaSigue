package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

public class RecuperaPasswordActivity extends AppCompatActivity {

    String TAG = "PUEBLA";
    TextView tv_titulo_toolbar, tv_nombre, tv_celular, tv_nuevopass, tv_nuevopass2;
    ImageView btn_back;
    Button btn_enviar;
    EditText et_nombre, et_celular, et_nuevopass, et_nuevopass2;
    ProgressDialog progressDialog;
    JSONArray jsonArr;
    String JsonResponse = null;
    String idusuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_nombre = findViewById(R.id.tv_motivo);
        tv_celular = findViewById(R.id.tv_celular);
        tv_nuevopass = findViewById(R.id.tv_password1);
        tv_nuevopass2 = findViewById(R.id.tv_password2);
        btn_back = findViewById(R.id.btn_back);
        btn_enviar = findViewById(R.id.btn_enviar);
        et_nombre = findViewById(R.id.et_nombre);
        et_celular = findViewById(R.id.et_celular);
        et_nuevopass = findViewById(R.id.et_nuevopass);
        et_nuevopass2 = findViewById(R.id.et_nuevopass2);

        tv_titulo_toolbar.setTypeface(tf);
        tv_nombre.setTypeface(tf);
        tv_celular.setTypeface(tf);
        tv_nuevopass.setTypeface(tf);
        tv_nuevopass2.setTypeface(tf);
        et_nombre.setTypeface(tf);
        et_celular.setTypeface(tf);
        et_nuevopass.setTypeface(tf);
        et_nuevopass2.setTypeface(tf);
        btn_enviar.setTypeface(tf);

        // lee datos del usuario
        /*String[] campos = new String[] {"idusuario", "nick", "nombre"};

        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        //Cursor c = db.query("Usuarios", campos, "idusuario=?", args, null, null, null);
        Cursor c = db.rawQuery("SELECT idusuario, nick, nombre FROM Usuarios", null);

        if (c.moveToFirst()) {
            Log.v(TAG, "hay cosas");
            //Recorremos el cursor hasta que no haya más registros
            do {
                idusuario = c.getString(0);
                Log.d(TAG, "idusr bd: " + idusuario);
            } while(c.moveToNext());
            c.close();
        }
        else{
            Log.v(TAG, "NO hay cosas");
        }
        db.close();
        //////*/

        btn_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_nombre.getText().toString();
                String cel = et_celular.getText().toString();
                String pass1 = et_nuevopass.getText().toString();
                String pass2 = et_nuevopass2.getText().toString();


                if(username.equals("")){
                    et_nombre.setError("Debes escribir tu nombre de usuario.");
                }
                if(cel.equals("")){
                    et_celular.setError("Debes escribir tu teléfono celular.");
                }
                if(pass1.equals("")){
                    et_nuevopass.setError("Debes escribir tu contraseña.");
                }
                if(pass2.equals("")){
                    et_nuevopass2.setError("Debes escribir otra vez tu contraseña.");
                }
                if(!pass1.equals(pass2)){
                    et_nuevopass2.setError("Las contraseñas no coinciden");
                }

                if(username.length() > 1 && cel.length() > 1 && pass1.length() > 1){
                    preparaEnvio(username, cel, pass1);
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

    private void preparaEnvio(String username, String cel, String newpass) {
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("username" , username);
            post_dict.put("cel", cel);
            post_dict.put("newpass", newpass);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            Log.v(TAG, "postdic len: " + String.valueOf(post_dict));

            progressDialog = new ProgressDialog(RecuperaPasswordActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Recuperando contraseña...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            recuperaPassword(String.valueOf(post_dict));
        }
    }

    private void recuperaPassword(String params) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String username = jsonObject.getString("username");
            String cel = jsonObject.getString("cel");
            String newpass = jsonObject.getString("newpass");

            jsonBody.put("username", username);
            jsonBody.put("cel", cel);
            jsonBody.put("newpass", newpass);

            final String requestBody = jsonBody.toString();
            Log.d(TAG, "params: " + requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CAMBIO_PASS_AFUERA_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "VOLLEY response recupera pass: " + response);
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

                        if(respuesta.equals("Aceptar")){
                            showAlert(mensaje);
                        }else{
                            showAlert(respuesta + ": " + mensaje);
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    // refresh
                    //showAlert("Su contraseña ha sido recuperada correctamente.");
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Puebla Sigue")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        //startActivity(getIntent());
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() { }
}
