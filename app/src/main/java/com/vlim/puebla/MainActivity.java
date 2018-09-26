package com.vlim.puebla;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView btn_cerrar_sesion, btn_ajustes, btn_nueveonce, btn_denunciaanonima, btn_telmujer, btn_botonpanico;
    Button btn_sigueme, btn_chat, btn_mensajesipm;
    TextView tv_version, tv_sigueme, tv_chat, tv_chat2, tv_mensajes, tv_botonmujer, tv_botonpanico, tv_servicios, tv_presionado;
    String TAG = "PUEBLA";
    // Toolbar
    TextView tv_titulo_toolbar;
    ImageView btn_nuevo_usuario;
    ImageButton btn_logout;
    View img_avisos;
    String nick = "", nombreUsuario = "", idusuario = "";
    Integer puedeRegistrar = 0;
    NetworkConnection nt_check;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 066;
    //Permisos
    public static final int REQUEST_CODE_CAMERA = 23;
    public static final int REQUEST_CODE_GPS = 24;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    Integer MY_LOCATION_REQUEST_CODE = 23;

    private long timeElapsed = 0L;
    private Toolbar toolbar;
    private int longClickDuration = 3000;  // 5 secgundos para el boton de panico
    Contador counter;

    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setBackgroundDrawableResource(R.drawable.background2);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        setSupportActionBar(toolbar);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");
        Typeface tf_bold = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedRegularBold.otf");

        final long[] startClickTime = new long[1];
        final boolean[] longClickActive = {false};

        btn_cerrar_sesion = findViewById(R.id.btn_salir);
        btn_ajustes = findViewById(R.id.btn_ajustes);
        btn_nueveonce = findViewById(R.id.btn_nueveonce);
        btn_denunciaanonima = findViewById(R.id.btn_denunciaanonima);
        btn_telmujer = findViewById(R.id.btn_telmujer);
        btn_botonpanico = findViewById(R.id.btn_panico);
        btn_sigueme = findViewById(R.id.btn_sigueme);
        btn_chat = findViewById(R.id.btn_chat_vecinal);
        btn_mensajesipm = findViewById(R.id.btn_mensajesipm);
        tv_titulo_toolbar = findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        tv_version = findViewById(R.id.tv_version);
        tv_version.setTypeface(tf);
        tv_version.setText("v" + BuildConfig.VERSION_NAME);
        tv_version.bringToFront();
        tv_sigueme = findViewById(R.id.tv_celular);
        tv_sigueme.setTypeface(tf_bold);
        tv_chat = findViewById(R.id.tv_password1);
        tv_chat.setTypeface(tf_bold);
        tv_chat2 = findViewById(R.id.tv_chat2);
        tv_chat2.setTypeface(tf);
        tv_mensajes = findViewById(R.id.tv_password2);
        tv_mensajes.setTypeface(tf_bold);
        tv_botonmujer = findViewById(R.id.tv_botonmujer);
        tv_botonmujer.setTypeface(tf);
        tv_botonpanico = findViewById(R.id.tv_botonpanico);
        tv_botonpanico.setTypeface(tf_bold);
        tv_servicios = findViewById(R.id.tv_motivo);
        tv_servicios.setTypeface(tf_bold);
        tv_presionado = findViewById(R.id.tv_presionado);
        tv_presionado.setTypeface(tf);

        // Get extras
        puedeRegistrar = getIntent().getExtras().getInt("isReg");

        // lee datos del usuario
        String[] campos = new String[] {"idusuario", "nick", "nombre"};

        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getReadableDatabase();
        //Cursor c = db.query("Usuarios", campos, "idusuario=?", args, null, null, null);
        Cursor c = db.rawQuery("SELECT idusuario, nick, nombre FROM Usuarios", null);

        if (c.moveToFirst()) {
            Log.v(TAG, "hay cosas");
            //Recorremos el cursor hasta que no haya más registros
            do {
                idusuario = c.getString(0);
                nick = c.getString(1);
                nombreUsuario = c.getString(2);
                //Toast.makeText(getApplicationContext(), idusuario + ", " + nick + ", " + nombre, Toast.LENGTH_LONG).show();
            } while(c.moveToNext());
        }
        else{
            Log.v(TAG, "NO hay cosas");
        }
        c.close();
        db.close();
        //////

        Log.d("consultausuario", nick + ", " + nombreUsuario + ", " + idusuario);

        // Check permisos
        compruebaPermisos();

        /////

        btn_cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                borraBD();
                Intent logout = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(logout);
            }
        });

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
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    Intent nueveOnceIntent = new Intent(MainActivity.this, Maps911Activity.class);
                    nueveOnceIntent.putExtra("idusuario", idusuario);
                    startActivity(nueveOnceIntent);
                }else{
                    Toast.makeText(getApplicationContext(), "Se requiere conexión a Internet.", Toast.LENGTH_LONG).show();
                }



            }
        });

        btn_denunciaanonima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprueba conexion a Internet
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    Intent secretarioIntent = new Intent(MainActivity.this, DenunciaAnonimaActivity.class);
                    secretarioIntent.putExtra("idusuario", idusuario);
                    startActivity(secretarioIntent);
                }else{
                    Toast.makeText(getApplicationContext(), "Se requiere conexión a Internet.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_telmujer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Llamando...");
                // dialog preguntar imagen o video
                final CharSequence[] options = {"Llamar a 01 800 624 2330", "Cancelar"};
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Tel Mujer");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Llamar a 01 800 624 2330")) {
                            Intent btnllamarTelMujer = new Intent(Intent.ACTION_CALL);
                            btnllamarTelMujer.setData(Uri.parse("tel:018006242330"));
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                Log.i(TAG, "checando permisos...");
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            startActivity(btnllamarTelMujer);
                        } else if (options[item].equals("Cancelar")) {
                            dialog.dismiss();
                        }
                        dialog.dismiss();
                    }
                });
                builder.show();


            }
        });

        btn_botonpanico.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    nt_check = new NetworkConnection(getApplicationContext());
                    if(nt_check.isOnline()){
                        counter = new Contador(3000,1000);
                        counter.start();
                    }else{
                        Toast.makeText(getApplicationContext(), "Se requiere conexión a Internet.", Toast.LENGTH_LONG).show();
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    counter.cancel();
                }
                return true;
            }
        });

        btn_sigueme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    Intent siguemeIntent = new Intent(MainActivity.this, Sigueme2Activity.class);
                    siguemeIntent.putExtra("idusuario", idusuario);
                    startActivity(siguemeIntent);
                }else{
                    Toast.makeText(getApplicationContext(), "Se requiere conexión a Internet.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprueba conexion a Internet
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    Intent btnredvecinal = new Intent(MainActivity.this, ChatVecinalActivity.class);
                    btnredvecinal.putExtra("idusuario", idusuario);
                    btnredvecinal.putExtra("refresh", "si");
                    startActivity(btnredvecinal);
                }else{
                    Toast.makeText(getApplicationContext(), "Se requiere conexión a Internet.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_mensajesipm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Comprueba conexion a Internet
                nt_check = new NetworkConnection(getApplicationContext());
                if(nt_check.isOnline()){
                    Intent secretarioIntent = new Intent(MainActivity.this, MensajesIPMActivity2.class);
                    secretarioIntent.putExtra("idusuario", idusuario);
                    secretarioIntent.putExtra("bandera", "0");
                    startActivity(secretarioIntent);
                }else{
                    Toast.makeText(getApplicationContext(), "Se requiere conexión a Internet.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void compruebaPermisos() {

        Log.i(TAG, "Verificando permisos");

        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();

        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("Ubicación");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
            permissionsNeeded.add("Conexión a Internet");
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Cámara");
        if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("Teléfono");
        if (!addPermission(permissionsList, Manifest.permission.VIBRATE))
            permissionsNeeded.add("Vibrar");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Escribir en memoria interna");
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("Leer memoria interna");
        /*if (!addPermission(permissionsList, Manifest.permission.MEDIA_CONTENT_CONTROL))
            permissionsNeeded.add("Control de medios");*/
        if (!addPermission(permissionsList, Manifest.permission.RECORD_AUDIO))
            permissionsNeeded.add("Grabar audio");
        if (!addPermission(permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("Enviar SMS");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "Es necesario otorgar permisos a " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            }
                        });
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
            return;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
        }
        return true;
    }

    private boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permiso necesario");
        alertBuilder.setMessage(msg + " es necesario habilitar el permiso");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.e(TAG,"Granted");
                }
                else{
                    Log.e(TAG,"Not Granted");
                }

                break;
        }
    }

    private void borraBD() {
        userSQLiteHelper usdbh =
                new userSQLiteHelper(this, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        db.delete("Usuarios", null, null);
        Log.i(TAG, "Elimina base Usuarios");
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public class Contador extends CountDownTimer {

        public Contador(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "Terminando de contar");
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                Intent btnpanico = new Intent(MainActivity.this, BotonPanicoActivity.class);
                btnpanico.putExtra("idusuario", idusuario);
                startActivity(btnpanico);

            } else {
                //Toast.makeText(getApplicationContext(), "Error en permiso mapa", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Error en permisos ubicacion");

                ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION },
                        MY_LOCATION_REQUEST_CODE);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(TAG, "Contando " + (millisUntilFinished/1000));
        }

    }

    public void notifica() {

        Log.d(TAG, "Enviando notificación");

        /*NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
        ncomp.setContentTitle("My Notification");
        ncomp.setContentText("Notification Listener Service Example");
        ncomp.setTicker("Notification Listener Service Example");
        ncomp.setSmallIcon(R.mipmap.ic_launcher);
        ncomp.setAutoCancel(true);
        nManager.notify((int)System.currentTimeMillis(),ncomp.build());*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.journaldev.com/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle("Notifications Title");
        builder.setContentText("Your notification content here.");
        builder.setSubText("Tap to view the website.");

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(1, builder.build());


    }

}
