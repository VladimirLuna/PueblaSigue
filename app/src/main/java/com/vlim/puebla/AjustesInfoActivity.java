package com.vlim.puebla;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AjustesInfoActivity extends AppCompatActivity {

    ImageView btn_info_personal, btn_contactos, btn_cambiar_pass;
    String TAG = "PUEBLA";
    // Toolbar
    TextView tv_titulo_toolbar;
    ImageView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_ajustes_info);
        getWindow().setBackgroundDrawableResource(R.drawable.background);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        // Toolbar
        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = findViewById(R.id.btn_back);
        btn_cambiar_pass = findViewById(R.id.btn_cambiar_pass);
        btn_contactos = findViewById(R.id.btn_contactos);
        btn_info_personal = findViewById(R.id.btn_ajustes_informacion);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_info_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent info_personal = new Intent(getApplicationContext(), InfoPersonalActivity.class);
                startActivity(info_personal);
            }
        });

        btn_contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactos = new Intent(getApplicationContext(), ContactosEmergenciaActivity.class);
                startActivity(contactos);
            }
        });

        btn_cambiar_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertPass = new AlertDialog.Builder(AjustesInfoActivity.this);
                alertPass.setTitle("Cambiar contraseña");
                LayoutInflater inflater = AjustesInfoActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog_pass, null);
                alertPass.setView(dialogView);

                final EditText et_pass1 = dialogView.findViewById(R.id.et_pass1);
                final EditText et_pass2 = dialogView.findViewById(R.id.et_pass2);
                final Button btn_guarda_pass = dialogView.findViewById(R.id.btn_guarda_pass);

                btn_guarda_pass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pass1 = et_pass1.getText().toString().trim();
                        String pass2 = et_pass2.getText().toString().trim();
                        if(pass1.equals("")){
                            et_pass1.setError("Debes escribir tu contraseña.");
                        }
                        if(pass2.equals("")){
                            et_pass2.setError("Debes escribir otra vez tu contraseña.");
                        }
                        if(!pass1.equals(pass2)){
                            et_pass2.setError("Las contraseñas no coinciden");
                        }
                    }
                });

                /*alertPass.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for OK button here

                    }
                });*/
                alertPass.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for CANCEL button here, or leave in blank
                    }
                });
                alertPass.show();
            }
        });

    }

    @Override
    public void onBackPressed() { }

}
