package com.vlim.puebla;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class Medios_1 extends Fragment {
    String TAG = "PUEBLA";
    private List<FotosBDModel> fotosList = new ArrayList<>();
    private RecyclerView fotosRecyclerView;
    private RecyclerViewHorizontalListAdapter fotosAdapter;
    String medioURL = "";
    //Constructor default
    public Medios_1(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageOne = inflater.inflate(R.layout.medios1, container, false);

        //Every you want add some VIEW you need add findViewId with the Inflater

        //Example
        /*
        TextView ExTV = (TextView)PageOne.findViewById(R.id.Something ID)
         */

        userSQLiteHelper mediadbh =
                new userSQLiteHelper(getContext(), "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = mediadbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT medio, tipo FROM Media", null);
        if (c.moveToFirst()) {
            Log.v(TAG, "hay medios");
            medioURL = c.getString(0);
            String tipo = c.getString(1);
            Log.i(TAG, "medio: " + medioURL + ", tipo: " + tipo );
        }

        ImageView img_fotos = PageOne.findViewById(R.id.img_fotos);
        Glide.with(getContext()).load(medioURL).into(img_fotos);
        img_fotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), "Fotos!!", Toast.LENGTH_LONG).show();

                    AlertDialog.Builder alertPass = new AlertDialog.Builder(getContext());
                    alertPass.setTitle("Medios");
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.custom_dialog_fotos, null);
                    alertPass.setView(dialogView);

                    fotosRecyclerView = dialogView.findViewById(R.id.idRecyclerViewHorizontalList);
                    // add a divider after each item for more clarity
                    fotosRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));
                    fotosAdapter = new RecyclerViewHorizontalListAdapter(fotosList, getContext());
                    LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    fotosRecyclerView.setLayoutManager(horizontalLayoutManager);
                    fotosRecyclerView.setAdapter(fotosAdapter);
                    populateFotosList();

                    alertPass.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Put actions for OK button here
                        }
                    });
                    /*alertPass.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Put actions for CANCEL button here, or leave in blank
                        }
                    });*/
                    alertPass.show();

                /*}
                else{
                    Log.v(TAG, "NO hay MEDIOS");
                }*/
            }
        });

        return PageOne;
    }

    private void populateFotosList(){

        userSQLiteHelper mediadbh =
                new userSQLiteHelper(getContext(), "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = mediadbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT medio, tipo FROM Media", null);
        if (c.moveToFirst()) {
            while(c.moveToNext()){
                Log.v(TAG, "populateFotosList hay medios");
                String medio = c.getString(0);
                String tipo = c.getString(1);

                Log.i(TAG, "Elemento agregado medio: " + medio + ", tipo: " + tipo );
                FotosBDModel medioURLitem = new FotosBDModel(medio, tipo);
                fotosList.add(medioURLitem);
            }
        }
        else{
            Toast.makeText(getContext(), "NO foto!", Toast.LENGTH_LONG).show();
        }

                /*imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(BitmapFactory.decodeFile(photoPathDB));
                galeriaPrev.setVisibility(View.VISIBLE);
                galeriaPrev.setImageBitmap(BitmapFactory.decodeFile(photoGaleryPathDB));
                db.close();*/

                    /*if(medio != null){
                        Log.i(TAG, "hay medio db");
                        /* imageCameraPreview.setVisibility(View.VISIBLE);*/
            /*btn_camara.setVisibility(View.INVISIBLE);*/
            ///btn_camara.setImageBitmap(BitmapFactory.decodeFile(fotoDB));
            //imageCameraPreview.setImageBitmap(BitmapFactory.decodeFile(fotoDB));
            /*Glide.with(this).load(fotoDB).into(imageCameraPreview);*/
                    /*}
                    else{
                        Log.i(TAG, "no hay medio db");
                        //btn_camara.setVisibility(View.INVISIBLE);
                        /*btn_camara.setVisibility(View.VISIBLE);*/
                    /*}

                   /* if(videoDB != null){
                        Log.i(TAG, "hay video db");
                        //btn_video.setVisibility(View.INVISIBLE);
                        /*imgvideoPrev.setVisibility(View.VISIBLE);*/
            /*tv_videocapturado.setVisibility(View.VISIBLE);*/
                   /* }
                    else{
                        Log.i(TAG, "no hay video db");
                        btn_video.setVisibility(View.VISIBLE);
                        /*tv_videocapturado.setVisibility(View.INVISIBLE);*/
            /*imgvideoPrev.setVisibility(View.INVISIBLE);*/
                    /*}

                    if(galeriaDB != null){
                        Log.i(TAG, "hay archivo galeria db");
                        /* galeriaPrev.setVisibility(View.VISIBLE);*/
                        /*if(galeriaDB.endsWith("mp4")){
                   /* btn_galeria.setVisibility(View.INVISIBLE);
                    galeriaPrev.setVisibility(View.VISIBLE);
                    galeriaPrev.setImageDrawable(getResources().getDrawable(R.drawable.boton_gris_circular));
                    tv_videocapturadogal.setVisibility(View.VISIBLE);*/
                       /* }
                        else{
                    /*btn_galeria.setVisibility(View.VISIBLE);
                    //galeriaPrev.setImageBitmap(BitmapFactory.decodeFile(galeriaDB));
                    Glide.with(this).load(galeriaDB).into(galeriaPrev);
                    tv_videocapturadogal.setVisibility(View.INVISIBLE);*/
            //tv_videocapturadogal.setText("Imagen \n Seleccionada");
                      /*  }
                    }
                    else{
                        Log.i(TAG, "no hay archivo galeria db");*/
                /*galeriaPrev.setVisibility(View.INVISIBLE);
                btn_galeria.setVisibility(View.VISIBLE);
                tv_videocapturadogal.setVisibility(View.INVISIBLE);*/
            /* }*/


        /*FotosBDModel potato = new FotosBDModel("Potato", R.drawable.agregar);
        FotosBDModel onion = new FotosBDModel("Onion", R.drawable.ajustes);
        FotosBDModel cabbage = new FotosBDModel("Cabbage", R.drawable.audio);
        FotosBDModel cauliflower = new FotosBDModel("Cauliflower", R.drawable.back);
        fotosList.add(potato);
        fotosList.add(onion);
        fotosList.add(cabbage);
        fotosList.add(cauliflower);*/
        fotosAdapter.notifyDataSetChanged();
    }
}
