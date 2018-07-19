package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class RegistroUsuarioActivity3 extends AppCompatActivity {

    String TAG = "PUEBLA";
    Typeface tf;
    TextView tv_titulo_toolbar, tv_mensaje, tv_nombrecompleto, tv_tel, tv_celular;
    ImageView btn_back;
    EditText et_nombrecomp, et_telefono, et_celular;
    ImageButton btn_agregainfante, btn_guardaregistro;
    String nombrecompleto, domicilio, telefono, celular, usuario, pass, tipo_identificacion, numid, nombredependiente="0", teldependiente="0", celdependiente="0", usrdependiente="0", passdependiente="0",
            nombrecontacto, telcontacto, celcontacto;
    ProgressDialog progressDialog;
    JSONArray jsonArr;
    String JsonResponse = null;
    String isdep = "0";
    String usrregistra = null;
    NetworkConnection nt_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario3);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        // recupera id usr
        // lee datos del usuario
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, 5);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT idusuario FROM Usuarios", null);
        String password = null;
        if (c.moveToFirst()) {
            Log.v(TAG, "hay cosas");
            usrregistra = c.getString(0);
            db.close();
        }
        else{
            Log.v(TAG, "NO hay cosas");
        }

        Intent i= getIntent();
        nombrecompleto = i.getStringExtra("nombrecompleto");
        domicilio = i.getStringExtra("domicilio");
        telefono = i.getStringExtra("telefono");
        celular = i.getStringExtra("celular");
        usuario = i.getStringExtra("usuario");
        pass = i.getStringExtra("pass");
        tipo_identificacion = i.getStringExtra("tipo_identificacion");
        numid = i.getStringExtra("numid");

        // Toolbar
        tv_titulo_toolbar = (TextView) findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        tv_mensaje = (TextView) findViewById(R.id.tv_mensaje);
        tv_mensaje.setTypeface(tf);
        tv_nombrecompleto = (TextView) findViewById(R.id.tv_nombrecompleto);
        tv_nombrecompleto.setTypeface(tf);
        tv_tel = (TextView) findViewById(R.id.tv_tel);
        tv_tel.setTypeface(tf);
        tv_celular = (TextView) findViewById(R.id.tv_usuario);
        tv_celular.setTypeface(tf);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        et_nombrecomp = (EditText) findViewById(R.id.et_nombrecontacto);
        et_nombrecomp.setTypeface(tf);
        et_telefono = (EditText) findViewById(R.id.et_telcontacto);
        et_telefono.setTypeface(tf);
        et_celular = (EditText) findViewById(R.id.et_celcontacto);
        et_celular.setTypeface(tf);
        btn_agregainfante = (ImageButton) findViewById(R.id.btn_enviardenuncia);
        btn_guardaregistro = (ImageButton) findViewById(R.id.btn_guardaregistro);

        btn_agregainfante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent datosCuentaIntent = new Intent(RegistroUsuarioActivity3.this, RegistroUsuarioActivity4.class);
                startActivityForResult(datosCuentaIntent, 1100);
            }
        });

        btn_guardaregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    nombrecontacto = et_nombrecomp.getText().toString().trim();
                    telcontacto = et_telefono.getText().toString().trim();
                    celcontacto = et_celular.getText().toString().trim();

                    if(nombrecontacto.length() < 1){
                        et_nombrecomp.setError("Ingresa el nombre completo de tu contacto.");
                    }
                    else if(celcontacto.length() < 1){
                        et_celular.setError("Ingresa el celular.");
                    }
                    else{
                        Log.i(TAG, "Prepara envio de registro de usuario");
                        preparaRegistroNuevoUsuario();
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

    private void preparaRegistroNuevoUsuario() {

        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put("nombrecompleto" , nombrecompleto);
            post_dict.put("domicilio", domicilio);
            post_dict.put("telefono", telefono);
            post_dict.put("celular", celular);
            post_dict.put("usuario", usuario);
            post_dict.put("pass", pass);
            post_dict.put("tipo_identificacion", tipo_identificacion);
            post_dict.put("numid", numid);
            post_dict.put("nombrecontacto", nombrecontacto);
            post_dict.put("telcontacto", telcontacto);
            post_dict.put("celcontacto", celcontacto);
            post_dict.put("nombredependiente", nombredependiente);
            post_dict.put("teldependiente", teldependiente);
            post_dict.put("celdependiente", celdependiente);
            post_dict.put("usrdependiente", usrdependiente);
            post_dict.put("passdependiente", passdependiente);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (post_dict.length() > 0) {
            progressDialog = new ProgressDialog(RegistroUsuarioActivity3.this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Guardando registro...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.show();
            registroNuevoUsuario(String.valueOf(post_dict));
        }
    }

    private void registroNuevoUsuario(String params) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            JSONObject jsonObject = new JSONObject(params);
            String nombrecompleto = jsonObject.getString("nombrecompleto");
            String domicilio = jsonObject.getString("domicilio");
            String telefono = jsonObject.getString("telefono");
            String celular = jsonObject.getString("celular");
            String usuario = jsonObject.getString("usuario");
            String pass = jsonObject.getString("pass");
            String tipo_identificacion = jsonObject.getString("tipo_identificacion");
            String numid = jsonObject.getString("numid");
            String nombrecontacto = jsonObject.getString("nombrecontacto");
            String telcontacto = jsonObject.getString("telcontacto");
            String celcontacto = jsonObject.getString("celcontacto");

            jsonBody.put("isdep", isdep);
            jsonBody.put("usrregistra", usrregistra);
            jsonBody.put("nombreCompleto", nombrecompleto);
            jsonBody.put("domicilio", domicilio);
            if(telefono.length() > 0){
                jsonBody.put("tel", telefono);
            }
            else{
                jsonBody.put("tel", "0");
            }
            jsonBody.put("cel", celular);
            jsonBody.put("nickname", usuario);
            jsonBody.put("passusuario", pass);
            jsonBody.put("tidienti", tipo_identificacion);
            jsonBody.put("numidenti", numid);
            jsonBody.put("nomContacto", nombrecontacto);
            if(telcontacto.length() > 0){
                jsonBody.put("telContacto", telcontacto);
            }
            else{
                jsonBody.put("telContacto", "0");
            }
            jsonBody.put("celContacto", celcontacto);
            jsonBody.put("nomDep", nombredependiente);
            jsonBody.put("telDep", teldependiente);
            jsonBody.put("celDep", celdependiente);
            jsonBody.put("nicknameDep", usrdependiente);
            jsonBody.put("passDep", passdependiente);

            final String requestBody = jsonBody.toString();
            Log.v(TAG, "Request nvoRegUsr: " + requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTRO_NUEVO_USUARIO_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        jsonArr = new JSONArray(response);
                        JsonResponse = response;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i(TAG, "VOLLEY response registro usuario: " + response);
                    progressDialog.dismiss();
                    showAlert();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "VOLLEY: " + error.toString());
                    progressDialog.dismiss();
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

    public void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Registro usuario");
        alertDialog.setMessage("El nuevo usuario se ha registrado correctamente");
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent intentBack = new Intent(getApplicationContext(), LoginActivity.class);
                intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentBack);
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "result: " + resultCode);

        if(resultCode == RESULT_CANCELED){
            Toast.makeText(this, "Sin dependientes.", Toast.LENGTH_LONG).show();
            nombredependiente = "0";
            teldependiente = "0";
            celdependiente = "0";
            usrdependiente = "0";
            passdependiente = "0";
            isdep = "0";
        }
        else if(requestCode == 1100)  {
            Toast.makeText(getApplicationContext(), "Infante registrado", Toast.LENGTH_LONG).show();
            nombredependiente = data.getExtras().getString("nombredependiente");
            teldependiente = data.getExtras().getString("teldependiente");
            if(teldependiente.length() < 1){
                teldependiente = "0";
            }
            celdependiente = data.getExtras().getString("celdependiente");
            usrdependiente = data.getExtras().getString("usrdependiente");
            passdependiente = data.getExtras().getString("passdependiente");
            isdep = "1";
            Log.i(TAG, "nombre: " + nombredependiente);
        }
    }

    @Override
    public void onBackPressed() { }
}
