package com.vlim.puebla;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

public class Medios_2 extends Fragment {
    String TAG = "PUEBLA";
    /*private List<FotosBDModel> videosList = new ArrayList<>();
    private RecyclerView videosRecyclerView;
    private RecyclerViewHorizontalListAdapter videosAdapter;*/
    String medioURL = "";
    //Constructor default
    public Medios_2(){};
    Context context;
    ImageView img_video;
    VideoView vid_preview;
    TextView tv_video;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageTwo = inflater.inflate(R.layout.medios2, container, false);

        img_video = PageTwo.findViewById(R.id.img_video);
        img_video.setEnabled(false);
        vid_preview = PageTwo.findViewById(R.id.vid_preview);
        tv_video = PageTwo.findViewById(R.id.tv_video);
        Typeface tf = Typeface.createFromAsset(PageTwo.getContext().getAssets(), "fonts/BoxedBook.otf");
        tv_video.setTypeface(tf);

        //Every you want add some VIEW you need add findViewId with the Inflater
        userSQLiteHelper mediadbh =
                new userSQLiteHelper(getContext(), "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = mediadbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT medio, tipo FROM Media WHERE tipo == 'video'", null);

        if (c.moveToFirst()) {
            Log.v(TAG, "hay videos! Medio2");
            medioURL = c.getString(0);
            String tipo = c.getString(1);
            Log.i(TAG, "medio: " + medioURL + ", tipo: " + tipo );
            img_video.setVisibility(View.INVISIBLE);
            //vid_preview.setVisibility(View.VISIBLE);
            tv_video.setVisibility(View.VISIBLE);
            //vid_preview.setVideoPath(medioURL);
            c.close();
        }
        else{
            Log.d(TAG, "No hay videos! Medios2");
        }

        tv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "abrir rollo de videos.");

                Intent videos = new Intent(getContext(), GaleriaVideosReporte911Activity.class);
                startActivityForResult(videos, Config.OPEN_GAL);

                /*AlertDialog.Builder alertPass = new AlertDialog.Builder(getContext());
                alertPass.setTitle("Videos");
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog_videos, null);
                alertPass.setView(dialogView);

                fotosRecyclerView = dialogView.findViewById(R.id.idRecyclerViewHorizontalList);
                // add a divider after each item for more clarity
                fotosRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));
                fotosAdapter = new RecyclerViewHorizontalListAdapter(fotosList, getContext());

                //populateFotosList();

                LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                fotosRecyclerView.setLayoutManager(horizontalLayoutManager);
                fotosRecyclerView.setAdapter(fotosAdapter);

                alertPass.setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Put actions for OK button here
                    }
                });
                    /*alertPass.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Put actions for CANCEL button here, or leave in blank
                        }
                    });*/
                /*alertPass.show();*/

                /*}
                else{
                    Log.v(TAG, "NO hay MEDIOS");
                }*/
            }
        });


        return PageTwo;
    }
}
