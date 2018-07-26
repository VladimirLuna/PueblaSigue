package com.vlim.puebla;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegistroUsuarioActivity4 extends AppCompatActivity {

    String TAG = "PUEBLA";
    Typeface tf;
    TextView tv_titulo_toolbar, tv_mensaje, tv_nombrecompleto, tv_tel, tv_cel, tv_usuario, tv_passs;
    ImageView btn_back;
    EditText et_nombredependiente, et_teldependiente, et_celdependiente, et_usrdependiente, et_passdependiente;
    ImageButton btn_agregainfante;
    NetworkConnection nt_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario4);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        // Toolbar
        tv_titulo_toolbar = (TextView) findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        tv_mensaje = (TextView) findViewById(R.id.tv_mensaje);
        tv_mensaje.setTypeface(tf);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        tv_nombrecompleto = (TextView) findViewById(R.id.tv_correo);
        tv_nombrecompleto.setTypeface(tf);
        tv_tel = (TextView) findViewById(R.id.tv_pass);
        tv_tel.setTypeface(tf);
        tv_cel = (TextView) findViewById(R.id.tv_identificacion);
        tv_cel.setTypeface(tf);
        tv_usuario = (TextView) findViewById(R.id.tv_numidentificacion);
        tv_usuario.setTypeface(tf);
        tv_passs = (TextView) findViewById(R.id.tv_passs);
        tv_passs.setTypeface(tf);
        et_nombredependiente = (EditText) findViewById(R.id.et_correo);
        et_nombredependiente.setTypeface(tf);
        et_teldependiente = (EditText) findViewById(R.id.et_password);
        et_teldependiente.setTypeface(tf);
        et_celdependiente = (EditText) findViewById(R.id.et_celdependiente);
        et_celdependiente.setTypeface(tf);
        et_usrdependiente = (EditText) findViewById(R.id.et_numidentificacion);
        et_usrdependiente.setTypeface(tf);
        et_passdependiente = (EditText) findViewById(R.id.et_passdependiente);
        et_passdependiente.setTypeface(tf);
        btn_agregainfante = (ImageButton) findViewById(R.id.btn_enviardenuncia);

        btn_agregainfante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    String nombredependiente = et_nombredependiente.getText().toString();
                    String teldependiente = et_teldependiente.getText().toString();
                    String celdependiente = et_celdependiente.getText().toString();
                    String usrdependiente = et_usrdependiente.getText().toString();
                    String passdependiente = et_passdependiente.getText().toString();

                    Intent intent = new Intent();
                    intent.putExtra("nombredependiente",nombredependiente);
                    intent.putExtra("teldependiente",teldependiente);
                    intent.putExtra("celdependiente",celdependiente);
                    intent.putExtra("usrdependiente",usrdependiente);
                    intent.putExtra("passdependiente",passdependiente);
                    setResult(1100,intent);
                    finish();
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

    @Override
    public void onBackPressed() { }
}
