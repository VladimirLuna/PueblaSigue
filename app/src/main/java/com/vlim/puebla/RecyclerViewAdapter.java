package com.vlim.puebla;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolderWarDetails> {

    // declaring variables
    private Context context;
    private List<ModelPublicaciones> listWarDetails;
    private String idPublicacion, imagenMensaje1, imagenMensaje2, imagenUsuario, urlVideo;

    RecyclerViewAdapter mListener;
    String TAG = "VLIMDEV";
    Typeface tf;

    //constructor
    RecyclerViewAdapter(Context context, List<ModelPublicaciones> listWarDetails) {
        this.context = context;
        this.listWarDetails = listWarDetails;
    }

    @Override
    public ViewHolderWarDetails onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(context).inflate(R.layout.list_war_details,parent,false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_publicaciones_details,parent,false);
        ViewHolderWarDetails viewHolderWarDetails = new ViewHolderWarDetails(view);

        /*viewHolderWarDetails.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("recycler", "position = " + viewHolderWarDetails.getAdapterPosition());
            }
        });*/


        //return viewHolderWarDetails;
        return new ViewHolderWarDetails(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderWarDetails holder, int position) {

        tf = Typeface.createFromAsset(context.getAssets(), "fonts/BoxedBook.otf");

        //setting data on each row of list according to position.

        holder.txtTitulo.setText(listWarDetails.get(position).getTitulo());
        holder.txtTitulo.setTypeface(tf);
        //holder.txtUsuario.setText("Usuario: " + listWarDetails.get(position).getUsuario());
        holder.txtDescripcion.setText(listWarDetails.get(position).getDescripcion());
        holder.txtDescripcion.setTypeface(tf);
        holder.txtFecha.setText(listWarDetails.get(position).getFecha());
        holder.txtFecha.setTypeface(tf);
        holder.txtVerComentarios.setTypeface(tf);
        idPublicacion = listWarDetails.get(position).getIdPublicacion();
        String ncomentarios = listWarDetails.get(position).getNComentarios();
        if(ncomentarios.equals("1")){
            holder.txtNComentarios.setText(ncomentarios + " comentario");
        }
        else{
            holder.txtNComentarios.setText(ncomentarios + " comentarios");
        }
        holder.txtNComentarios.setTypeface(tf);

        //holder.txtDefenderKing.setText(listWarDetails.get(position).getDefenderKing());
        //holder.img_publicacion.setText(listWarDetails.get(position).getDefenderKing());
        //holder.img_publicacion.setImageURI().setImageBitmap(BitmapFactory.decodeStream(listWarDetails.get(position).getImagen()));

        imagenMensaje1 = listWarDetails.get(position).getImagenMensaje();
        imagenMensaje2 = listWarDetails.get(position).getImagen2Mensaje();
        imagenUsuario = listWarDetails.get(position).getImagenUsuario();
        Picasso.with(context).load(imagenMensaje1).into(holder.img_publicacion);
        Picasso.with(context).load(imagenUsuario).into(holder.img_usuario);

        // Uri uri = Uri.parse(listWarDetails.get(position).getImagenMensaje());
        //
        urlVideo = listWarDetails.get(position).getVideoMensaje();
        if(urlVideo != null){
            Log.i(TAG, "urlvideo: " + urlVideo);
            holder.video_pub.setVisibility(View.VISIBLE);
            /*holder.video_pub.setVideoURI(Uri.parse(uri_video));
            holder.video_pub.setMediaController(new MediaController(holder.mContext));*/
            //holder.video_pub.setVideoPath(uri_video);
        }
    }
    //returns list size

    @Override
    public int getItemCount() {
        return listWarDetails.size();
    }

    public void setClickListener(ItemClickListener listener) {
        this.mListener = (RecyclerViewAdapter) listener;
    }

    class ViewHolderWarDetails extends RecyclerView.ViewHolder {

        // declaring variables
        TextView txtTitulo, txtUsuario, txtDescripcion, txtFecha, txtNComentarios, txtVerComentarios;
        ImageView img_publicacion, img_publicacion2, img_usuario, video_pub;

        private Context mContext;
        String idPublicacion = null;

        ViewHolderWarDetails(View v) {
            super(v);

            txtTitulo = (TextView) v.findViewById(R.id.txtTitulo);
            txtFecha = (TextView) v.findViewById(R.id.txtFecha);
            txtDescripcion = (TextView) v.findViewById(R.id.txtDescripcion);
            txtNComentarios = (TextView) v.findViewById(R.id.txtNumComentarios);
            txtVerComentarios = (TextView) v.findViewById(R.id.txt_vercomentarios);
            img_usuario = (ImageView) v.findViewById(R.id.img_usuario);
            img_publicacion = (ImageView) v.findViewById(R.id.img_publicacion);
            img_publicacion2 = (ImageView) v.findViewById(R.id.img_publicacion2);
            video_pub = (ImageView) v.findViewById(R.id.img_video);

            video_pub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    urlVideo = listWarDetails.get(getLayoutPosition()).getVideoMensaje();
                    Log.d(TAG, "Video url para ver: " + urlVideo);
                    Intent videoIntent = new Intent(v.getContext(), VeImagenActivity.class);
                    videoIntent.putExtra("urlvideo", urlVideo);
                    videoIntent.putExtra("urlfoto", "0");
                    v.getContext().startActivity(videoIntent);
                }
            });

            img_publicacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imagenMensaje1 = listWarDetails.get(getLayoutPosition()).getImagenMensaje();
                    Log.d(TAG, "Imagen para ver: " + imagenMensaje1);
                    if(imagenMensaje1 != null){
                        Intent imagebiggerIntent = new Intent(v.getContext(), VeImagenActivity.class);
                        imagebiggerIntent.putExtra("urlvideo", "0");
                        imagebiggerIntent.putExtra("urlfoto", imagenMensaje1);
                        v.getContext().startActivity(imagebiggerIntent);
                    }
                }
            });

            txtVerComentarios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    idPublicacion = listWarDetails.get(getLayoutPosition()).getIdPublicacion();
                    Log.d(TAG, "idpublicacion: " + idPublicacion);
                    Intent comentariosIntent = new Intent(v.getContext(), ComentariosActivity.class);
                    comentariosIntent.putExtra("idPublicacion", idPublicacion);
                    ((ChatVecinalActivity)context).finish();
                    v.getContext().startActivity(comentariosIntent);

                }
            });

            txtNComentarios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* get datos */
                    idPublicacion = listWarDetails.get(getLayoutPosition()).getIdPublicacion();
                    Log.d(TAG, "idpublicacion: " + idPublicacion);
                    Intent comentariosIntent = new Intent(v.getContext(), ComentariosActivity.class);
                    comentariosIntent.putExtra("idPublicacion", idPublicacion);
                    ((ChatVecinalActivity)context).finish();
                    v.getContext().startActivity(comentariosIntent);
                }
            });

        }


        /*@Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("position", String.valueOf(position));
            if(position != RecyclerView.NO_POSITION) {
                Toast.makeText(mContext, "Recycler", Toast.LENGTH_LONG).show();
                Log.d("position", String.valueOf(position));
                /*AnuncioPhoto anuncioPhoto = mAnuncioPhotos[position];
                Intent intent = new Intent(mContext, ComentariosActivity.class);
                intent.putExtra(ChatVecinalActivity.EXTRA_SPACE_PHOTO, anuncioPhoto);
                startActivity(intent);*/
            /*}
        }*/
    }

    public interface ItemClickListener {
        void onClickItem(int pos);

        void onLongClickItem(int pos);
    }
}