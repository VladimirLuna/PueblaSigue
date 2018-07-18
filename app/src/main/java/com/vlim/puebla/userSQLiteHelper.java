package com.vlim.puebla;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class userSQLiteHelper extends SQLiteOpenHelper {
    //Sentencia SQL para crear la tabla de Usuarios
    private String sqlCreate = "CREATE TABLE Usuarios (idusuario INTEGER, nick TEXT, nombre TEXT, usr TEXT, password TEXT, img TEXT)";
    private String sqlMediosCreate = "CREATE TABLE Media (idmedio INTEGER, medio TEXT, tipo TEXT)";   // tipo: foto, video, audio
    private String sqlMediosChatCreate = "CREATE TABLE MediaChat (idmedio INTEGER, photopath TEXT, videopath TEXT, galeriapath TEXT)";

    public userSQLiteHelper(Context contexto, String nombre,
                            SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creación de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlMediosChatCreate);
        db.execSQL(sqlMediosCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //NOTA: Por simplicidad del ejemplo aquí utilizamos directamente la opción de
        //      eliminar la tabla anterior y crearla de nuevo vacía con el nuevo formato.
        //      Sin embargo lo normal será que haya que migrar datos de la tabla antigua
        //      a la nueva, por lo que este método debería ser más elaborado.

        //Se elimina la versión anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        db.execSQL("DROP TABLE IF EXISTS MediaChat");
        db.execSQL("DROP TABLE IF EXISTS Media");

        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
        db.execSQL(sqlMediosChatCreate);
        db.execSQL(sqlMediosCreate);
    }
}
