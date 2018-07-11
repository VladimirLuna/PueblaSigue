package com.vlim.puebla;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RecuperaPasswordActivity extends AppCompatActivity {

    String TAG = "PUEBLA";
    TextView tv_titulo_toolbar, tv_nombre, tv_celular, tv_nuevopass, tv_nuevopass2;
    ImageView btn_back;
    Button btn_enviar;
    EditText et_nombre, et_celular, et_nuevopass, et_nuevopass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_nombre = findViewById(R.id.tv_motivo);
        tv_celular = findViewById(R.id.tv_celular);
        tv_nuevopass = findViewById(R.id.tv_nuevopass);
        tv_nuevopass2 = findViewById(R.id.tv_nuevopass2);
        btn_back = findViewById(R.id.btn_back);
        btn_enviar = findViewById(R.id.btn_enviar);
        et_nombre = findViewById(R.id.et_nombre);
        et_celular = findViewById(R.id.et_celular);
        et_nuevopass = findViewById(R.id.et_nuevopass);
        et_nuevopass2 = findViewById(R.id.et_nuevopass2);

        tv_titulo_toolbar.setTypeface(tf);
        tv_nombre.setTypeface(tf);
        tv_celular.setTypeface(tf);
        tv_nuevopass.setTypeface(tf);
        tv_nuevopass2.setTypeface(tf);
        et_nombre.setTypeface(tf);
        et_celular.setTypeface(tf);
        et_nuevopass.setTypeface(tf);
        et_nuevopass2.setTypeface(tf);
        btn_enviar.setTypeface(tf);


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
