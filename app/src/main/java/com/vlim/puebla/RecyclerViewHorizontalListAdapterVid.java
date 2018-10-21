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

class RecyclerViewHorizontalListAdapterVid extends RecyclerView.Adapter<RecyclerViewHorizontalListAdapterVid.ImagesViewHolder>{
    private List<VideosBDModel> horizontalVideosList;
    Context context;
    String TAG = "PUEBLA";

    public RecyclerViewHorizontalListAdapterVid(List<VideosBDModel> horizontalVideosList, Context context){
        this.horizontalVideosList= horizontalVideosList;
        this.context = context;
    }

    @Override
    public RecyclerViewHorizontalListAdapterVid.ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View imagesProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.videos_item, parent, false);
        RecyclerViewHorizontalListAdapterVid.ImagesViewHolder gvh = new RecyclerViewHorizontalListAdapterVid.ImagesViewHolder(imagesProductView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHorizontalListAdapterVid.ImagesViewHolder holder, final int position) {
        //holder.imageView.setImageResource(horizontalFotosList.get(position).getMedioImage());
        //holder.imageView.setImageDrawable(Drawable.createFromPath(horizontalFotosList.get(position).getMedioImage()));
        //Glide.with(context).load(horizontalVideosList.get(position).getMedioVideo()).into(holder.mVideoView);

        //holder.mVideoView.setVideoURI(Uri.parse(horizontalVideosList.get(position).getMedioVideo()));

        Glide.with(context)
                .load("file:///" + horizontalVideosList.get(position).getMedioVideo())
                .into(holder.videoThumb);

        Log.d("PUEBLA", "agrega videos!: " + horizontalVideosList.get(position).getMedioVideo());
        /*holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "photo is selected", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Foto: " + horizontalFotosList.get(position).getMedioImage());
            }
        });*/

        /*holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "Video: " + horizontalVideosList.get(position).getMedioVideo());
                eliminaFoto(horizontalVideosList.get(position).getMedioVideo(), v);
                return false;
            }
        });*/

        holder.btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminaVideo(horizontalVideosList.get(position).getMedioVideo(), v);
            }
        });
    }

    private void eliminaVideo(String medioImage, View v) {
        userSQLiteHelper mediadbh =
                new userSQLiteHelper(context, "DBUsuarios", null, Config.VERSION_DB);
        SQLiteDatabase db = mediadbh.getWritableDatabase();
        db.execSQL("DELETE FROM Media WHERE medio = '"+medioImage+"'");
        db.close();
        Log.d(TAG, "Video eliminado");
        Toast.makeText(v.getContext(), "Video eliminado", Toast.LENGTH_LONG).show();


    }

    @Override
    public int getItemCount() {
        return horizontalVideosList.size();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumb;
        TextView btn_eliminar;
        public ImagesViewHolder(View view) {
            super(view);
            videoThumb = view.findViewById(R.id.video_thumb);
            btn_eliminar = view.findViewById(R.id.btn_eliminar);
        }
    }
}
