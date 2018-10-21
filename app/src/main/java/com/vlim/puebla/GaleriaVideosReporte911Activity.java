package com.vlim.puebla;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GaleriaVideosReporte911Activity extends AppCompatActivity {

    public static final String TAG = "PUEBLA";
    private RecyclerViewHorizontalListAdapterVid videosAdapter;
    private RecyclerView videosRecyclerView;
    private List<VideosBDModel> videosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria_videos_reporte911);

        AlertDialog.Builder alertPass = new AlertDialog.Builder(this);
        alertPass.setTitle("Videos agregados");
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_videos, null);
        alertPass.setView(dialogView);

        videosRecyclerView = dialogView.findViewById(R.id.idRecyclerViewHorizontalList);
        // add a divider after each item for more clarity
        videosRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.HORIZONTAL));

        videosAdapter = new RecyclerViewHorizontalListAdapterVid(videosList, getApplicationContext());

        populateVideosList();

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        videosRecyclerView.setLayoutManager(horizontalLayoutManager);
        videosRecyclerView.setAdapter(videosAdapter);

        alertPass.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Put actions for OK button here
                Log.d(TAG, "closeeeee");
                finish();
            }
        });
        alertPass.show();
    }

    private void populateVideosList(){
        int i = 0;
        videosList.clear();
        userSQLiteHelper mediadbh =
                new userSQLiteHelper(getApplicationContext(), "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = mediadbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT medio, tipo FROM Media WHERE tipo == 'video'", null);
        if (c.moveToFirst()) {
            //Log.v(TAG, "foto: " + c.getString(0) + ", tipo: " + c.getString(1));
            while (!c.isAfterLast()) {
                i++;
                Log.v(TAG, "populateVideosList hay medios " + i);
                String medio = c.getString(0);
                String tipo = c.getString(1);

                Log.i(TAG, "Elemento agregado video: " + medio + ", tipo: " + tipo );
                VideosBDModel medioURLitem = new VideosBDModel(medio, tipo);
                videosList.add(medioURLitem);

                c.moveToNext();
            }
            c.close();
        }
        else{
            //Toast.makeText(getContext(), "NO foto!", Toast.LENGTH_LONG).show();
            Log.d(TAG,"No hay videos!");
        }

        videosAdapter.notifyDataSetChanged();
    }
}


