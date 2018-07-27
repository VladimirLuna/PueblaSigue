package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
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

public class DetalleContactoActivity extends AppCompatActivity {

    String TAG = "PUEBLA";
    JSONArray jsonArr;
    String JsonResponse = null;
    private ProgressDialog pDialog;
    String id_contacto;
    TextView tv_detalle, tv_titulo_toolbar, tv_nombre;
    ImageView btn_back, img_borrar, img_editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_contacto);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Bundle datosContacto = getIntent().getExtras();
        id_contacto = datosContacto.getString("id_grupo");

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        tv_detalle = findViewById(R.id.tv_detalle);
        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_nombre = findViewById(R.id.tv_motivo);
        btn_back = findViewById(R.id.btn_back);
        img_borrar = findViewById(R.id.img_borrar);
        img_editar = findViewById(R.id.img_editar);

        tv_detalle.setTypeface(tf);
        tv_titulo_toolbar.setTypeface(tf);

        obtieneDetalleContacto(id_contacto);

        img_borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        DetalleContactoActivity.this);
                //builder.setTitle("DoxSteel");
                builder.setTitle(Html.fromHtml("<font color='#303F9F'>DoxSteel</font>"));
                builder.setMessage(Html.fromHtml("<font color='black'>¿Estás seguro que deseas eliminar este contacto de emergencia?</font>"));
                builder.setNegativeButton("Si",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // Eliminando contacto de emergencia
                                eliminaContacto(id_contacto);
                            }
                        });
                builder.setPositiveButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                finish();
                                /*Intent logout = new Intent(DetalleContactoActivity.this, LoginActivity.class);
                                startActivity(logout);*/
                            }
                        });
                builder.show();
            }
        });

        img_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editarContacto = new Intent(DetalleContactoActivity.this, EditarContactoActivity.class);
                editarContacto.putExtra("id_contacto", id_contacto);
                startActivityForResult(editarContacto, 33);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 33){   // agregar usuario
            finish();
            startActivity(getIntent());
        }
    }

    private void eliminaContacto(String id_contacto) {
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Eliminando contacto de emergencia...");
        pDialog.show();

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            final JSONObject jsonBody = new JSONObject();

            jsonBody.put("idcontacto", id_contacto);
            final String requestBody = jsonBody.toString();
            Log.d(TAG, "borra contacto: " + requestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ELIMINA_CONTACTO_URL, new Response.Listener<String>() {
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

                                if(jsonObj.getString("respuesta").equals("OK")){
                                    showAlert(jsonObj.getString("mensaje"));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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

    private void showAlert(String message) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Aviso")
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

                             tv_nombre.setText(nombre_completo);
                             tv_detalle.setText("Teléfono: " + telefono + "\nCelular: " + celular + "\nCorreo electrónico: \n" + correo_contacto);

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
