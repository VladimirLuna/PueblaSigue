package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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

public class RegistroUsuarioActivity3 extends AppCompatActivity {

    String TAG = "PUEBLA";
    Typeface tf;
    TextView tv_titulo_toolbar, tv_mensaje, tv_nombrecompleto, tv_tel, tv_celular;
    ImageView btn_back;
    EditText et_nombrecomp, et_telefono, et_celular, et_correocontacto;
    ImageView btn_guardaregistro;
    String nombrecompleto, domicilio, telefono, celular, usuario, pass, tipo_identificacion, numid, nombrecontacto, telcontacto, celcontacto, correoContacto, condicionMedica, colonia;
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

        Intent i= getIntent();
        nombrecompleto = i.getStringExtra("nombrecompleto");
        domicilio = i.getStringExtra("domicilio");
        telefono = i.getStringExtra("telefono");
        celular = i.getStringExtra("celular");
        usuario = i.getStringExtra("correo");
        pass = i.getStringExtra("pass");
        tipo_identificacion = i.getStringExtra("tipo_identificacion");
        numid = i.getStringExtra("numid");
        colonia = i.getStringExtra("colonia");
        condicionMedica = i.getStringExtra("padecimientos");

        // Toolbar
        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        tv_mensaje = findViewById(R.id.tv_mensaje);
        tv_mensaje.setTypeface(tf);
        tv_nombrecompleto = findViewById(R.id.tv_correo);
        tv_nombrecompleto.setTypeface(tf);
        tv_tel = findViewById(R.id.tv_pass);
        tv_tel.setTypeface(tf);
        tv_celular = findViewById(R.id.tv_numidentificacion);
        tv_celular.setTypeface(tf);
        btn_back = findViewById(R.id.btn_back);
        et_nombrecomp = findViewById(R.id.et_correo);
        et_nombrecomp.setTypeface(tf);
        et_telefono = findViewById(R.id.et_password);
        et_telefono.setTypeface(tf);
        et_celular = findViewById(R.id.et_numidentificacion);
        et_celular.setTypeface(tf);
        et_correocontacto = findViewById(R.id.et_correocontacto);
        et_correocontacto.setTypeface(tf);
        btn_guardaregistro = findViewById(R.id.btn_guardaregistro);

        btn_guardaregistro.setOnClickListener(new View.OnClickListener() {
            int flag = 0;
            @Override
            public void onClick(View v) {
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    nombrecontacto = et_nombrecomp.getText().toString().trim();
                    telcontacto = et_telefono.getText().toString().trim();
                    celcontacto = et_celular.getText().toString().trim();
                    correoContacto = et_correocontacto.getText().toString();

                    if(celular.equals(celcontacto)){
                        et_celular.setError("El número celular del contacto de emergencia debe ser distinto a tu teléfono celular.");
                        flag = 0;
                    }
                    else{
                        flag = 1;
                    }

                    if(usuario.equals(correoContacto)){
                        et_correocontacto.setError("El correo electrónico del contacto de emergencia debe ser distinto a tu correo electrónico.");
                        flag = 0;
                    }
                    else{
                        flag = 1;
                    }

                    if(nombrecontacto.length() < 1){
                        et_nombrecomp.setError("Ingresa el nombre completo de tu contacto.");
                        flag=0;
                    }
                    else if(celcontacto.length() < 1){
                        et_celular.setError("Ingresa el celular.");
                        flag=0;
                    }
                    else{
                        flag=1;
                    }

                    if(flag==1){
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
            post_dict.put("isdep" , "0");
            post_dict.put("nombreCompleto" , nombrecompleto);
            post_dict.put("domicilio", domicilio);
            post_dict.put("tel", telefono);
            post_dict.put("cel", celular);
            post_dict.put("nickname", usuario);
            post_dict.put("passusuario", pass);
            post_dict.put("tidienti", tipo_identificacion);
            post_dict.put("numidenti", numid);
            post_dict.put("nomContacto", nombrecontacto);
            post_dict.put("telContacto", telcontacto);
            post_dict.put("celContacto", celcontacto);
            post_dict.put("correoContacto", correoContacto);
            post_dict.put("condicionMedica", condicionMedica);
            post_dict.put("comunidad", colonia);
            post_dict.put("usrregistra", "0");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "envia: " + post_dict.toString());
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

            String isdep = jsonObject.getString("isdep");
            String nombreCompleto = jsonObject.getString("nombreCompleto");
            String domicilio = jsonObject.getString("domicilio");
            String tel = jsonObject.getString("tel");
            String cel = jsonObject.getString("cel");
            String nickname = jsonObject.getString("nickname");
            String passusuario = jsonObject.getString("passusuario");
            String tidienti = jsonObject.getString("tidienti");
            String numidenti = jsonObject.getString("numidenti");
            String nomContacto = jsonObject.getString("nomContacto");
            String telContacto = jsonObject.getString("telContacto");
            String celContacto = jsonObject.getString("celContacto");
            String correoContacto = jsonObject.getString("correoContacto");
            String condicionMedica = jsonObject.getString("condicionMedica");
            String comunidad = jsonObject.getString("comunidad");
            String usrregistra = jsonObject.getString("usrregistra");

            jsonBody.put("isdep", isdep);
            jsonBody.put("nombreCompleto", nombreCompleto);
            jsonBody.put("domicilio", domicilio);
            if(telefono.length() > 0){
                jsonBody.put("tel", tel);
            }
            else{
                jsonBody.put("tel", "0");
            }
            jsonBody.put("cel", cel);
            jsonBody.put("nickname", nickname);
            jsonBody.put("passusuario", passusuario);
            jsonBody.put("tidienti", tidienti);
            jsonBody.put("numidenti", numidenti);
            jsonBody.put("nomContacto", nomContacto);
            if(telContacto.length() > 0){
                jsonBody.put("telContacto", telContacto);
            }
            else{
                jsonBody.put("telContacto", "0");
            }
            jsonBody.put("celContacto", celContacto);
            jsonBody.put("correoContacto", correoContacto);
            jsonBody.put("condicionMedica", condicionMedica);
            jsonBody.put("comunidad", comunidad);
            jsonBody.put("usrregistra", usrregistra);

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
    public void onBackPressed() { }
}