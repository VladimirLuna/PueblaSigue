package com.vlim.puebla;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.MotionEvent;
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
import java.util.ArrayList;

public class MensajesIPMActivity2 extends ListActivity{

    ////private ListView listView;
    boolean myMessage = false;
    String idusuario = "";
    JSONArray jsonArr;
    String JsonResponse = null;
    String id_mensaje_conv = "", id_conversacion = "", conversacion = "", autor = "";
    String TAG = "PUEBLA";
    TextView tv_titulo_toolbar;
    EditText msg_type;
    Button btn_chat_send;
    ImageView btn_back, btn_refresh;
    private SwipeRefreshLayout swipeRefreshLayout;
    NetworkConnection nt_check;
    Contador counter;
    int tiempo = 30000;
    ProgressDialog progressDialog;

    ArrayList<Message> messages;
    AwesomeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_mensajes_ipm2);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        Intent i= getIntent();
        idusuario = i.getStringExtra("idusuario");

        ///listView = findViewById(R.id.list_msg);
        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        msg_type = findViewById(R.id.msg_type);
        msg_type.setTypeface(tf);
        btn_chat_send = findViewById(R.id.btn_chat_send);
        btn_chat_send.setTypeface(tf);
        btn_back = findViewById(R.id.btn_back);
        btn_refresh = findViewById(R.id.btn_refresh);

        //set ListView adapter first
        /*adapter = new MessageAdapter(this, R.layout.left_chat_bubble, ChatBubbles);
        listView.setAdapter(adapter);*/

        // contador
        nt_check = new NetworkConnection(getApplicationContext());
        if(nt_check.isOnline()){
            counter = new Contador(tiempo,1000);
            counter.start();
        }else{
            Toast.makeText(getApplicationContext(), "Se requiere conexión a Internet.", Toast.LENGTH_LONG).show();
        }

        obtieneComentarios();

        btn_chat_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msg_type.getText().toString().trim().equals("")) {
                    //Toast.makeText(MensajesSecretarioActivity.this, "Por favor escribe tu mensaje...", Toast.LENGTH_SHORT).show();
                    msg_type.setError("Error. Escribe tu mensaje por favor.");
                } else {
                    //add message to list
                    Log.i("bandera", "true");
                    //chatSecretario(editText.getText().toString(), myMessage);
                    /*editText.setFocusable(false);
                    editText.setText("");*/

                    /*ChatBubble ChatBubble = new ChatBubble(editText.getText().toString(), true);
                    ChatBubbles.add(ChatBubble);
                    adapter.notifyDataSetChanged();
                    editText.setText("");*/
                    /*if (myMessage) {
                        myMessage = false;
                    } else {
                        myMessage = true;
                    }*/
                    nuevoMensajeSecretario(id_conversacion, msg_type.getText().toString(), idusuario);
                }
            }
        });

        msg_type.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*if(event.getAction() == MotionEvent.ACTION_UP){
                    Log.d(TAG, "Escribiendo...");
                }
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d(TAG, "Dejando de escribir...");
                }*/
                counter.cancel();
                return false;
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*startActivity(getIntent());
                finish();*/
                finish();
                Intent secretarioIntent = new Intent(MensajesIPMActivity2.this, MensajesIPMActivity2.class);
                secretarioIntent.putExtra("idusuario", idusuario);
                startActivity(secretarioIntent);
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter.cancel();
                finish();
            }
        });

    }

    private void nuevoMensajeSecretario(String id_conversacion, String conversacion, String autor) {
        //Toast.makeText(getApplicationContext(), "idconv: " + id_conversacion + ", conversacion: " + conversacion + ", autor: " + autor, Toast.LENGTH_LONG).show();

        JSONObject post_dict = new JSONObject();
        try {
            post_dict.put("idconv", id_conversacion);
            post_dict.put("conversacion", conversacion);
            post_dict.put("autor", autor);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (post_dict.length() > 0) {
            Log.v("nuevomensajesecre", String.valueOf(post_dict));
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                final JSONObject jsonBody = new JSONObject();

                JSONObject jsonObject = new JSONObject(String.valueOf(post_dict));
                String idconvSend = jsonObject.getString("idconv");
                String conversacionSend = jsonObject.getString("conversacion");
                String autorSend = jsonObject.getString("autor");

                jsonBody.put("idconv", idconvSend);
                jsonBody.put("conversacion", conversacionSend);
                jsonBody.put("idautor", autorSend);

                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.NUEVO_MENSAJE_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonArr = new JSONArray(response);
                            JsonResponse = response;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.i("VOLLEYresponse", String.valueOf(jsonArr));
                        Log.i("VOLLEYresponse", response);

                        // recarga activity
                        finish();
                        startActivity(getIntent());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error en la conexión, intente de nuevo.", Toast.LENGTH_LONG).show();
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
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void obtieneComentarios() {
        progressDialog = new ProgressDialog(MensajesIPMActivity2.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Leyendo mensajes...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();

        JSONObject post_dict = new JSONObject();
        try {
            post_dict.put("idusr", idusuario);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (post_dict.length() > 0) {
            Log.v(TAG, String.valueOf(post_dict));
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                final JSONObject jsonBody = new JSONObject();

                JSONObject jsonObject = new JSONObject(String.valueOf(post_dict));
                String idusr = jsonObject.getString("idusr");
                jsonBody.put("idusr", idusr);
                final String requestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_MENSAJES_SECRETARIO_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonArr = new JSONArray(response);
                            JsonResponse = response;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //Log.i("VOLLEYresponse", String.valueOf(jsonArr));
                        Log.i("VOLLEYresponse", response);

                        leeMensajes(response);


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Error en la conexión, intente de nuevo.", Toast.LENGTH_LONG).show();
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
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void leeMensajes(String response) {
        //Log.i("mensajesecre", response);
        String autorIPM = null;
        messages = new ArrayList<Message>();
        try {
            JSONArray array = new JSONArray(response);
            JSONObject obj;

            for (int i = 0; i < array.length(); i++) {
                obj = array.getJSONObject(i);
                id_mensaje_conv = obj.getString("id_mensaje_conv");
                id_conversacion = obj.getString("id_conversacion");
                conversacion = obj.getString("conversacion");
                autor = obj.getString("autor");

                Log.i("secre", id_mensaje_conv + ", " + conversacion + ", " + id_conversacion + ", " + autor);
                Log.d(TAG, "lado autor: " + Integer.valueOf(autor));

                /*if(autor.equals(autorIPM)){
                    Log.d(TAG, "bubble IPM. left: " + conversacion);
                    ChatBubble ChatBubble = new ChatBubble(conversacion, true, "left");
                    ChatBubbles.add(ChatBubble);
                    adapter.notifyDataSetChanged();
                    msg_type.setText("");
                }
                else{
                    Log.d(TAG, "bubble usuario. right: " + conversacion);
                    ChatBubble ChatBubble = new ChatBubble(conversacion, false, "right");
                    ChatBubbles.add(ChatBubble);
                    adapter.notifyDataSetChanged();
                    msg_type.setText("");
                }*/

                if(autor.equals(idusuario)){
                   messages.add(new Message(conversacion, true));
                }
                else{
                    messages.add(new Message(conversacion, false));
                }

            } // for

            adapter = new AwesomeAdapter(getApplicationContext(), messages);
            setListAdapter(adapter);

            progressDialog.dismiss();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() { }

    public class Contador extends CountDownTimer {

        public Contador(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "Terminando de contar");
            // Reload
            finish();
            //startActivity(getIntent());
            Intent secretarioIntent = new Intent(MensajesIPMActivity2.this, MensajesIPMActivity2.class);
            secretarioIntent.putExtra("idusuario", idusuario);
            startActivity(secretarioIntent);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            //Log.d(TAG, "Contando " + (millisUntilFinished/1000));
        }

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        Log.d(TAG, "deteniendo contador");
        counter.cancel();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
        //counter = new Contador(tiempo,1000);
        Log.d(TAG, "Reiniciando contador");
        counter.start();
        super.onStop();
    }
}
