package com.vlim.puebla;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView btn_back, btn_ajustes, btn_nueveonce, btn_denunciaanonima, btn_telmujer, btn_botonpanico;
    Button btn_sigueme, btn_chat, btn_mensajesipm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.background2);

        btn_back = findViewById(R.id.btn_back);
        btn_ajustes = findViewById(R.id.btn_ajustes);
        btn_nueveonce = findViewById(R.id.btn_nueveonce);
        btn_denunciaanonima = findViewById(R.id.btn_denunciaanonima);
        btn_telmujer = findViewById(R.id.btn_telmujer);
        btn_botonpanico = findViewById(R.id.btn_panico);
        btn_sigueme = findViewById(R.id.btn_sigueme);
        btn_chat = findViewById(R.id.btn_chat_vecinal);
        btn_mensajesipm = findViewById(R.id.btn_mensajesipm);

        btn_ajustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent registroIntent = new Intent(MainActivity.this, RegistroUsuarioActivity.class);
                startActivity(registroIntent);*/
                Intent ajustesIntent = new Intent(MainActivity.this, AjustesActivity.class);
                startActivity(ajustesIntent);
            }
        });

        btn_nueveonce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nueveOnceIntent = new Intent(MainActivity.this, Maps911Activity.class);
                startActivity(nueveOnceIntent);

            }
        });
    }
}
