package com.vlim.puebla;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    Button btn_login;
    EditText et_usuario, et_password;
    TextView tv_usuario, tv_pass, tv_olvidepass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login);
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        tv_usuario = findViewById(R.id.tv_usuario);
        tv_pass = findViewById(R.id.tv_pass);
        tv_olvidepass = findViewById(R.id.tv_olvidepass);
        et_usuario = findViewById(R.id.et_usuario);
        et_password = findViewById(R.id.et_passusuario);
        btn_login = findViewById(R.id.btn_login);
        tv_usuario.setTypeface(tf);
        tv_pass.setTypeface(tf);
        tv_olvidepass.setTypeface(tf);
        et_usuario.setTypeface(tf);
        et_password.setTypeface(tf);
        btn_login.setTypeface(tf);
    }
}
