package com.vlim.puebla;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewHorizontalListAdapter extends RecyclerView.Adapter<RecyclerViewHorizontalListAdapter.ImagesViewHolder>{
    private List<FotosBDModel> horizontalFotosList;
    Context context;

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
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "photo is selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return horizontalFotosList.size();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImagesViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_foto);
        }
    }
}
