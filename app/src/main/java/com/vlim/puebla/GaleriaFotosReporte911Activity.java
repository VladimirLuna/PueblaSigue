package com.vlim.puebla;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GaleriaFotosReporte911Activity extends AppCompatActivity {

    public static final String TAG = "PUEBLA";
    //private List<FotosBDModel> fotosList = new ArrayList<>();
    RecyclerView fotosRecyclerView;
    List<ModelGaleria> listFotos = new ArrayList<>();
    LinearLayoutManager llmg;
    RecyclerViewAdapterGaleria adapterGaleria;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria_fotos_reporte911);

        fotosRecyclerView = findViewById(R.id.recyclerViewGaleria);

        llmg = new LinearLayoutManager(this);
        fotosRecyclerView.setLayoutManager(llmg);

        leeMedios();

    }

    private void leeMedios() {
        progressDialog = new ProgressDialog(GaleriaFotosReporte911Activity.this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Leyendo galería, por favor espere");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();

        ModelGaleria modelGaleria = new ModelGaleria();

        int i = 0;
        userSQLiteHelper mediadbh =
                new userSQLiteHelper(getApplicationContext(), "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = mediadbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT idmedio, medio, tipo FROM Media WHERE tipo == 'foto'", null);
        if (c.moveToFirst()) {
            //Log.v(TAG, "foto: " + c.getString(0) + ", tipo: " + c.getString(1));
            while (!c.isAfterLast()) {
                i++;
                Log.v(TAG, "populateFotosList hay medios gal " + i);
                String idmedio = c.getString(0);
                String medio = c.getString(1);
                String tipo = c.getString(2);

                Log.i(TAG, "Elemento agregado medio: " + medio + ", tipo: " + tipo);

                modelGaleria.setImagen(medio);
                modelGaleria.setIDImagen(idmedio);
                listFotos.add(modelGaleria);

                c.moveToNext();
            }
            c.close();
        } else {
            //Toast.makeText(getContext(), "NO foto!", Toast.LENGTH_LONG).show();
            Log.d(TAG, "No hay fotos!");
        }

        Log.d(TAG, "lista fotos: " + listFotos.size());

        adapterGaleria = new RecyclerViewAdapterGaleria(listFotos, GaleriaFotosReporte911Activity.this);
        fotosRecyclerView.setAdapter(adapterGaleria);
        adapterGaleria.notifyDataSetChanged();

        progressDialog.dismiss();
    }


}
