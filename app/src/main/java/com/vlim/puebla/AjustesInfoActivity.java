package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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

public class AjustesInfoActivity extends AppCompatActivity {

    ImageView btn_info_personal, btn_contactos, btn_cambiar_pass;
    String TAG = "PUEBLA";
    // Toolbar
    TextView tv_titulo_toolbar;
    ImageView btn_back;
    String idusuario = "";
    ProgressDialog progressDialog;
    JSONArray jsonArr;
    String JsonResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_ajustes_info);
        //getWindow().setBackgroundDrawableResource(R.drawable.background);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        // lee datos del usuario
        String[] campos = new String[] {"idusuario", "nick", "nombre"};

        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        //Cursor c = db.query("Usuarios", campos, "idusuario=?", args, null, null, null);
        Cursor c = db.rawQuery("SELECT idusuario, nick, nombre FROM Usuarios", null);

        if (c.moveToFirst()) {
            Log.v("SQL23", "hay cosas");
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

        // Toolbar
        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = findViewById(R.id.btn_back);
        btn_cambiar_pass = findViewById(R.id.btn_cambiar_pass);
        btn_contactos = findViewById(R.id.btn_contactos);
        btn_info_personal = findViewById(R.id.btn_ajustes_informacion);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_info_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info_personal = new Intent(getApplicationContext(), InfoPersonalActivity.class);
                startActivity(info_personal);
            }
        });

        btn_contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactos = new Intent(getApplicationContext(), ContactosEmergenciaActivity.class);
                startActivity(contactos);
            }
        });

        btn_cambiar_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertPass = new AlertDialog.Builder(AjustesInfoActivity.this);
                alertPass.setTitle("Cambiar contraseña");
                LayoutInflater inflater = AjustesInfoActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog_pass, null);
                alertPass.setView(dialogView);

                final EditText et_pass1 = dialogView.findViewById(R.id.et_pass1);
                final EditText et_pass2 = dialogView.findViewById(R.id.et_pass2);
                final Button btn_guarda_pass = dialogView.findViewById(R.id.btn_guarda_pass);

                btn_guarda_pass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pass1 = et_pass1.getText().toString().trim();
                        String pass2 = et_pass2.getText().toString().trim();
                        if(pass1.equals("")){
                            et_pass1.setError("Debes escribir tu contraseña.");
                        }
                        if(pass2.equals("")){
                            et_pass2.setError("Debes escribir otra vez tu contraseña.");
                        }
                        if(!pass1.equals(pass2)){
                            et_pass2.setError("Las contraseñas no coinciden");
                        }
                        else{
                            preparaEnvio(idusuario, pass1);
                        }
                    }
                });

                alertPass.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for CANCEL button here, or leave in blank
                    }
                });
                alertPass.show();
            }
        });

    }

    private void preparaEnvio(String idusuario, String pass) {
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("idusr" , idusuario);
            post_dict.put("newpass", pass);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            Log.v(TAG, "postdic len: " + String.valueOf(post_dict));

            progressDialog = new ProgressDialog(AjustesInfoActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Actualizando contraseña...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            cambioPassword(String.valueOf(post_dict));
        }
    }

    private void cambioPassword(String params) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String idusr = jsonObject.getString("idusr");
            String newpass = jsonObject.getString("newpass");

            jsonBody.put("idusr", idusr);
            jsonBody.put("newpass", newpass);
            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.AJUSTES_CAMBIO_PASS_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Log.i("VOLLEYresponse", String.valueOf(jsonArr));
                    Log.i(TAG, "VOLLEY response cambio pass: " + response);
                    progressDialog.dismiss();

                    // refresh
                    showAlert("Su contraseña ha sido actualizada correctamente.");


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
                        startActivity(getIntent());
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() { }

}
