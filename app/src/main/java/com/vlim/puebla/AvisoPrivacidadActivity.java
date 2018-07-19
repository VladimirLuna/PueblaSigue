package com.vlim.puebla;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

public class AvisoPrivacidadActivity extends AppCompatActivity {

    TextView tv_titulo, tv_regresar;
    PDFView pdfView;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aviso_privacidad);

        tv_titulo = findViewById(R.id.tv_titulo_toolbar);
        tv_regresar = findViewById(R.id.tv_regresar);
        btn_back = findViewById(R.id.btn_back);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");
        tv_titulo.setTypeface(tf);
        tv_regresar.setTypeface(tf);

        tv_regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        pdfView=findViewById(R.id.pdfv);
        pdfView.fromAsset("PROTECCION DE DATOS PERSONALES Puebla.pdf").load();
    }

    @Override
    public void onBackPressed() { }
}
