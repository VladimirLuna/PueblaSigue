package com.vlim.puebla;

import android.content.Context;
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

public class MediosAnonima_3 extends Fragment {
    String TAG = "PUEBLA";
    String medioURL = "";
    //Constructor default
    public MediosAnonima_3(){};
    Context context;
    ImageView img_audio;
    TextView tv_audio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageTree = inflater.inflate(R.layout.mediosanonima3, container, false);

        img_audio = PageTree.findViewById(R.id.img_audio);
        img_audio.setEnabled(false);
        tv_audio = PageTree.findViewById(R.id.tv_audio);
        Typeface tf = Typeface.createFromAsset(PageTree.getContext().getAssets(), "fonts/BoxedBook.otf");
        tv_audio.setTypeface(tf);

        //Every you want add some VIEW you need add findViewId with the Inflater
        userSQLiteHelper mediadbh =
                new userSQLiteHelper(getContext(), "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = mediadbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT medio, tipo FROM Media WHERE tipo == 'audio'", null);

        if (c.moveToFirst()) {
            Log.v(TAG, "hay audio! Medio3");
            medioURL = c.getString(0);
            String tipo = c.getString(1);
            Log.i(TAG, "medio: " + medioURL + ", tipo: " + tipo );
            img_audio.setVisibility(View.INVISIBLE);
            tv_audio.setVisibility(View.VISIBLE);

            c.close();
        }
        else{
            Log.d(TAG, "No hay audio! Medios3");
        }

        return PageTree;
    }
}
