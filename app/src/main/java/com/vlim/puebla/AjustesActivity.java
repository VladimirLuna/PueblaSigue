package com.vlim.puebla;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AjustesActivity extends AppCompatActivity {

    TextView tv_titulo_toolbar, tv_info, tv_chat, tv_mensaje1;
    ImageView btn_back, btn_ajustes_informacion, btn_ajustes_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = findViewById(R.id.btn_back);
        btn_ajustes_informacion = findViewById(R.id.btn_ajustes_informacion);
        btn_ajustes_chat = findViewById(R.id.btn_contactos);
        tv_info = findViewById(R.id.tv_info);
        tv_chat = findViewById(R.id.tv_password1);
        tv_mensaje1 = findViewById(R.id.tv_motivo);
        tv_info.setTypeface(tf);
        tv_chat.setTypeface(tf);
        tv_mensaje1.setTypeface(tf);

        btn_ajustes_informacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ajustesInfo = new Intent(AjustesActivity.this, AjustesInfoActivity.class);
                startActivity(ajustesInfo);
            }
        });

        btn_ajustes_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ajustesChat = new Intent(AjustesActivity.this, AjustesChatActivity.class);
                startActivity(ajustesChat);
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
    public void onBackPressed() { }
}
