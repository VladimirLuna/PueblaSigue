package com.vlim.puebla;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.List;

public class MensajesIPMActivity extends AppCompatActivity {

    private ListView listView;
    private View btnSend;
    private EditText editText;
    boolean myMessage = false;
    private List<ChatBubble> ChatBubbles;
    private ArrayAdapter<ChatBubble> adapter;
    String idusuario = "";
    JSONArray jsonArr;
    String JsonResponse = null;
    String id_mensaje_conv = "", id_conversacion = "", conversacion = "", autor = "";
    String TAG = "PUEBLA";
    TextView tv_titulo_toolbar;
    EditText msg_type;
    Button btn_chat_send;
    ImageView btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_mensajes_ipm);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        Intent i= getIntent();
        idusuario = i.getStringExtra("idusuario");

        ChatBubbles = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list_msg);
        btnSend = findViewById(R.id.btn_chat_send);
        editText = (EditText) findViewById(R.id.msg_type);
        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        msg_type = findViewById(R.id.msg_type);
        msg_type.setTypeface(tf);
        btn_chat_send = findViewById(R.id.btn_chat_send);
        btn_chat_send.setTypeface(tf);
        btn_back = findViewById(R.id.btn_back);

        //set ListView adapter first
        adapter = new MessageAdapter(this, R.layout.left_chat_bubble, ChatBubbles);
        listView.setAdapter(adapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    //Toast.makeText(MensajesSecretarioActivity.this, "Por favor escribe tu mensaje...", Toast.LENGTH_SHORT).show();
                    editText.setError("Error. Escribe tu mensaje por favor.");
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
                    nuevoMensajeSecretario(id_conversacion, editText.getText().toString(), idusuario);
                }
            }
        });

        obtieneComentarios();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        JSONObject post_dict = new JSONObject();
        try {
            post_dict.put("idusr", idusuario);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (post_dict.length() > 0) {
            Log.v("secretariosend", String.valueOf(post_dict));
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
        Log.i("mensajesecre", response);
        Integer autorPrevio = 0;
        try {
            JSONArray array = new JSONArray(response);
            JSONObject obj;
            for (int i = 0; i < array.length(); i++) {
                obj = array.getJSONObject(i);
                id_mensaje_conv = obj.getString("id_mensaje_conv");
                id_conversacion = obj.getString("id_conversacion");
                conversacion = obj.getString("conversacion");
                autor = obj.getString("autor");

                //Log.i("bandera", "false");
                //chatSecretario(conversacion, false);
                Log.i("secre", id_mensaje_conv + ", " + conversacion + ", " + id_conversacion + ", " + autor);

                if(Integer.valueOf(autor) > autorPrevio){
                    ChatBubble ChatBubble = new ChatBubble(conversacion, true);
                    ChatBubbles.add(ChatBubble);
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                }
                else{
                    ChatBubble ChatBubble = new ChatBubble(conversacion, false);
                    ChatBubbles.add(ChatBubble);
                    adapter.notifyDataSetChanged();
                    editText.setText("");
                }

                autorPrevio = Integer.valueOf(autor);

                /*if (myMessage) {
                    myMessage = false;
                } else {
                    myMessage = true;
                }*/
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() { }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chatvecinal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_regresar:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

}
