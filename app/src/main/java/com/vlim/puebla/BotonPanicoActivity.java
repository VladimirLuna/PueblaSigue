package com.vlim.puebla;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class BotonPanicoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boton_panico);
    }

    @Override
    public void onBackPressed() { }
}
