package com.vlim.puebla;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewHorizontalListAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalListAdapter.ImagesViewHolder>{
    private List<FotosBDModel> horizontalFotosList;
    Context context;
    String TAG = "PUEBLA";

    public RecyclerViewHorizontalListAdapter(List<FotosBDModel> horizontalFotosList, Context context){
        this.horizontalFotosList= horizontalFotosList;
        this.context = context;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View imagesProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fotos_item, parent, false);
        ImagesViewHolder gvh = new ImagesViewHolder(imagesProductView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, final int position) {
        //holder.imageView.setImageResource(horizontalFotosList.get(position).getMedioImage());
        //holder.imageView.setImageDrawable(Drawable.createFromPath(horizontalFotosList.get(position).getMedioImage()));
        Glide.with(context).load(horizontalFotosList.get(position).getMedioImage()).into(holder.imageView);

        Log.d("PUEBLA", "agrega fotos!: " + horizontalFotosList.get(position).getMedioImage());
        /*holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "photo is selected", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Foto: " + horizontalFotosList.get(position).getMedioImage());
            }
        });*/

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "Foto: " + horizontalFotosList.get(position).getMedioImage());
                eliminaFoto(horizontalFotosList.get(position).getMedioImage(), v);
                return false;
            }
        });

        holder.btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminaFoto(horizontalFotosList.get(position).getMedioImage(), v);
            }
        });
    }

    private void eliminaFoto(String medioImage, View v) {
        userSQLiteHelper mediadbh =
                new userSQLiteHelper(context, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = mediadbh.getWritableDatabase();
        db.execSQL("DELETE FROM Media WHERE medio = '"+medioImage+"'");
        db.close();
        Log.d(TAG, "Imagen eliminada");
        Toast.makeText(v.getContext(), "Imagen eliminada", Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        return horizontalFotosList.size();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView btn_eliminar;
        public ImagesViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_foto);
            btn_eliminar = view.findViewById(R.id.btn_eliminar);
        }
    }
}
