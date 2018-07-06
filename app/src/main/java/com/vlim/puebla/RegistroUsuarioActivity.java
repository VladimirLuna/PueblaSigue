package com.vlim.puebla;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class RegistroUsuarioActivity extends AppCompatActivity {

    String TAG = "PUEBLA";
    TextView tv_titulo_toolbar, tv_nombre, tv_domicilio, tv_telefono, tv_celular, tv_avisoprivacidad, tv_terminoscondiciones;
    Switch switch_notificaciones, switch_acepto;
    ImageView btn_back, btn_siguiente;
    EditText et_nombre, et_domicilio, et_telparticular, et_celular;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_domicilio = findViewById(R.id.tv_domicilio);
        tv_telefono = findViewById(R.id.tv_telefono);
        tv_celular = findViewById(R.id.tv_celular);
        tv_avisoprivacidad = findViewById(R.id.tv_avisoprivacidad);
        tv_terminoscondiciones = findViewById(R.id.tv_terminoscondiciones);

        tv_nombre = findViewById(R.id.tv_nombrecompleto);
        tv_nombre.setTypeface(tf);
        tv_domicilio = findViewById(R.id.tv_domicilio);
        tv_domicilio.setTypeface(tf);
        tv_telefono = findViewById(R.id.tv_telefono);
        tv_telefono.setTypeface(tf);
        tv_celular = findViewById(R.id.tv_celular);
        tv_celular.setTypeface(tf);
        et_nombre = findViewById(R.id.et_nombreusuario);
        et_nombre.setTypeface(tf);
        et_domicilio = findViewById(R.id.et_domicilio);
        et_domicilio.setTypeface(tf);
        et_telparticular = findViewById(R.id.et_telefono);
        et_telparticular.setTypeface(tf);
        et_celular = findViewById(R.id.et_celular);
        et_celular.setTypeface(tf);
        switch_notificaciones = findViewById(R.id.switch_notificaciones);
        switch_acepto = findViewById(R.id.switch_acepto);
        btn_back = findViewById(R.id.btn_back);
        btn_siguiente = findViewById(R.id.btn_siguiente);

        tv_titulo_toolbar.setTypeface(tf);
        switch_notificaciones.setTypeface(tf);
        switch_acepto.setTypeface(tf);

        btn_siguiente.setEnabled(false);
        btn_siguiente.getBackground().setAlpha(55);


        switch_acepto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    btn_siguiente.setEnabled(b);
                    btn_siguiente.getBackground().setAlpha(255);
                }
                else{
                    btn_siguiente.getBackground().setAlpha(70);
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

    @Override
    public void onBackPressed() { }
}
