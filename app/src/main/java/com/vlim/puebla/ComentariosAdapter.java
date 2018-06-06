package com.vlim.puebla;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

class ComentariosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    List<DataComentarios> data= Collections.emptyList();
    DataComentarios current;
    int currentPos=0;
    Typeface tf;

    // create constructor to innitilize context and data sent from MainActivity
    public ComentariosAdapter(Context context, List<DataComentarios> data){
        this.context=context;
        inflater= LayoutInflater.from(context);
        this.data=data;
    }

    // Inflate the layout when viewholder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.container_comentarios_general, parent,false);
        MyHolder holder=new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        tf = Typeface.createFromAsset(context.getAssets(), "fonts/BoxedBook.otf");

        // Get current position of item in recyclerview to bind data and assign values from list
        MyHolder myHolder= (MyHolder) holder;
        DataComentarios current = data.get(position);
        myHolder.textNick.setText(current.nickUsuario);
        myHolder.textNick.setTypeface(tf, Typeface.BOLD);
        myHolder.textComentario.setText(current.comentario);
        myHolder.textComentario.setTypeface(tf);
        myHolder.textFecha.setText(current.fecha);
        myHolder.textFecha.setTypeface(tf);
        myHolder.textFecha.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{

        TextView textNick;
        //ImageView ivFish;
        TextView textComentario;
        TextView textFecha;

        // create constructor to get widget reference
        public MyHolder(View itemView) {
            super(itemView);
            textNick= (TextView) itemView.findViewById(R.id.textNick);
            //ivFish= (ImageView) itemView.findViewById(R.id.ivFish);
            textComentario = (TextView) itemView.findViewById(R.id.textComentario);
            textFecha = (TextView) itemView.findViewById(R.id.textFecha);
        }

    }

}