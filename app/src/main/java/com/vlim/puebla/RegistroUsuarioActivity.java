package com.vlim.puebla;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistroUsuarioActivity extends AppCompatActivity {

    String TAG = "PUEBLA";
    TextView tv_titulo_toolbar, tv_nombre, tv_domicilio, tv_telefono, tv_celular, tv_avisoprivacidad, tv_terminoscondiciones;
    Switch switch_notificaciones, switch_acepto;
    ImageView btn_back, btn_datoscuenta;
    EditText et_nombre, et_domicilio, et_telparticular, et_celular;

    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    LocationManager locationManager;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    private final static int ALL_PERMISSIONS_RESULT = 101;
    NetworkConnection nt_check;

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

        tv_nombre = findViewById(R.id.tv_correo);
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
        btn_datoscuenta = findViewById(R.id.btn_datoscuenta);

        tv_titulo_toolbar.setTypeface(tf);
        switch_notificaciones.setTypeface(tf);
        switch_acepto.setTypeface(tf);

        btn_datoscuenta.setEnabled(false);
        btn_datoscuenta.getBackground().setAlpha(55);

        tv_avisoprivacidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent avisoPrivacidadIntent = new Intent(RegistroUsuarioActivity.this, AvisoPrivacidadActivity.class);
                startActivity(avisoPrivacidadIntent);
            }
        });

        tv_terminoscondiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent terminosCondicionesIntent = new Intent(RegistroUsuarioActivity.this, TerminosyCondicionesActivity.class);
                startActivity(terminosCondicionesIntent);
            }
        });

        /* Check Internet conn*/
        if(!isOnline()){
            finish();
            Toast.makeText(getApplicationContext(), "Es necesario tener conexión a Internet.", Toast.LENGTH_LONG).show();
        }

        switch_acepto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    btn_datoscuenta.setEnabled(b);
                    btn_datoscuenta.getBackground().setAlpha(255);
                }
                else{
                    btn_datoscuenta.getBackground().setAlpha(70);
                }

            }
        });

        btn_datoscuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    String nombreCompleto = et_nombre.getText().toString().trim();
                    String domicilio = et_domicilio.getText().toString().trim();
                    String telefono = et_telparticular.getText().toString().trim();
                    String celular = et_celular.getText().toString().trim();

                    if(nombreCompleto.length() < 1){
                        et_nombre.setError("Escriba el nombre.");
                    }
                    else if(domicilio.length() <= 5){
                        et_domicilio.setError("Escriba el domicilio.");
                    }
                    else if(celular.length() <= 5){
                        et_celular.setError("Teléfono celular no válido.");
                    }
                    else{
                        if(!validaNombre(nombreCompleto)){
                            Log.d(TAG, "Error, nombre con caracteres no válidos");
                            et_nombre.setError("Nombre incorrecto");
                        }
                        else{
                            if(!validaTelefono(celular)){
                                et_celular.setError("Teléfono celular no válido");
                                }
                                else{
                                    Log.i(TAG, "Datos correctos");
                                    Intent datosCuentaIntent = new Intent(RegistroUsuarioActivity.this, RegistroUsuarioActivity2.class);
                                    datosCuentaIntent.putExtra("nombrecompleto", nombreCompleto);
                                    datosCuentaIntent.putExtra("domicilio", domicilio);
                                    datosCuentaIntent.putExtra("telefono", telefono);
                                    datosCuentaIntent.putExtra("celular", celular);
                                    startActivity(datosCuentaIntent);
                                }

                        }
                    }
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

    // valida nombres
    public static boolean validaNombre( String nombre )
    {
        String regx = "^[a-zA-Z\\sáéíóúñüàèñ]{3,35}$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(nombre);
        return matcher.find();
    }

    public static boolean validaTelefono( String tel )
    {
        String regx = "^[0-9]{5,10}$";
        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(tel);
        return matcher.find();
    }

    @Override
    public void onBackPressed() { }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("El GPS está deshabilitado");
        alertDialog.setMessage("¿Desea habilitar el GPS?");
        alertDialog.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "No rejected permissions.");
                    canGetLocation = true;
                }
                break;
            // Gestionar el resto de permisos
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(RegistroUsuarioActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
