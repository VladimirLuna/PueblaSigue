package com.vlim.puebla;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecyclerViewAdapterGaleria extends RecyclerView.Adapter<RecyclerViewAdapterGaleria.ImagesViewHolder>{
    private List<ModelGaleria> GaleriaFotosList;
    Context context;
    String TAG = "PUEBLA";

    public RecyclerViewAdapterGaleria(List<ModelGaleria> horizontalFotosList, Context context){
        this.GaleriaFotosList= horizontalFotosList;
        this.context = context;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View imagesProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_fotos_db_details, parent, false);
        ImagesViewHolder gvh = new ImagesViewHolder(imagesProductView);
        return gvh;
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, final int position) {
        //holder.imageView.setImageResource(horizontalFotosList.get(position).getMedioImage());
        //holder.imageView.setImageDrawable(Drawable.createFromPath(horizontalFotosList.get(position).getMedioImage()));
        Glide.with(context).load(GaleriaFotosList.get(position).getImagen()).into(holder.imageView);

        Log.d("PUEBLA", "agrega fotos!: " + GaleriaFotosList.get(position).getImagen() + ", id: " + GaleriaFotosList.get(position).getIDImagen());
        /*holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Foto seleccionada " , Toast.LENGTH_SHORT).show();

            }
        });*/
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "long clic: " +  GaleriaFotosList.get(position).getImagen());
                return false;
            }
        });

        holder.btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "elimina imagen: " +  GaleriaFotosList.get(position).getImagen() );
                //eliminaFoto(GaleriaFotosList.get(position).getImagen());
            }
        });
    }



    @Override
    public int getItemCount() {
        return GaleriaFotosList.size();
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView btn_eliminar;
        public ImagesViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_foto);
            btn_eliminar =  view.findViewById(R.id.btn_eliminar);
            /*btn_eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String foto = GaleriaFotosList.get(getLayoutPosition()).getImagen();
                    Log.d(TAG, "eliminar imagen: " + foto);


                }
            });*/
        }
    }

    private void eliminaFoto(String foto) {
        Log.d(TAG, "elimina: " + foto);
    }
}


//public class RecyclerViewAdapterGaleria extends RecyclerView.Adapter<RecyclerViewAdapterGaleria.ViewHolderWarDetails> {
/*public class RecyclerViewAdapterGaleria extends RecyclerView.Adapter<RecyclerViewAdapterGaleria.ImagesViewHolder>{

    // declaring variables
    private Context context;
    private List<ModelGaleria> listWarDetailsGal;
    private String imagenGaleria;

    RecyclerViewAdapterGaleria mListener;
    String TAG = "PUEBLA";

    //constructor
    public RecyclerViewAdapterGaleria(Context context, List<ModelGaleria> listWarDetails) {
        this.context = context;
        this.listWarDetailsGal = listWarDetails;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate the layout file
        View imagesProductView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fotos_item, parent, false);
        RecyclerViewHorizontalListAdapter.ImagesViewHolder gvh = new ImagesViewHolder(imagesProductView);
        return gvh;
    }

    @Override
    public ViewHolderWarDetails onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(context).inflate(R.layout.list_war_details,parent,false);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_fotos_db_details,parent,false);
        ViewHolderWarDetails viewHolderWarDetails = new ViewHolderWarDetails(view);

        //return viewHolderWarDetails;
        return new ViewHolderWarDetails(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderWarDetails holder, int position) {

        //setting data on each row of list according to position.
        imagenGaleria = listWarDetailsGal.get(position).getImagen();
        Log.d(TAG, "imagen " + listWarDetailsGal.get(position).getIDImagen());
        //Picasso.with(context).load(imagenGaleria).into(holder.img_galeria);

    }
    //returns list size

    @Override
    public int getItemCount() {
        return listWarDetailsGal.size();
    }

    public void setClickListener(ItemClickListener listener) {
        this.mListener = (RecyclerViewAdapterGaleria) listener;
    }

    class ViewHolderWarDetails extends RecyclerView.ViewHolder {

        // declaring variables
        TextView btn_eliminar;
        ImageView img_galeria;

        private Context mContext;
        String idPublicacion = null;

        ViewHolderWarDetails(View v) {
            super(v);

            btn_eliminar =  v.findViewById(R.id.btn_eliminar);
            btn_eliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //idPublicacion = listWarDetails.get(getLayoutPosition()).getIdPublicacion();
                    Log.d(TAG, "eliminar imagen: " + idPublicacion);


                }
            });
        }
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImagesViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.img_foto);
        }
    }

    public interface ItemClickListener {
        void onClickItem(int pos);

        void onLongClickItem(int pos);
    }
}*/

