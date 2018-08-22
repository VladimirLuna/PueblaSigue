package com.vlim.puebla;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CustomContactosListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private OnCustomClickListener callback;
    private List<Contactos> contactoItems;
    String TAG = "PUEBLA";
    Contactos contacts;
    Context context;


    public CustomContactosListAdapter(Activity activity, List<Contactos> contactoItems, OnCustomClickListener callback) {
        this.activity = activity;
        this.contactoItems = contactoItems;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return contactoItems.size();
    }

    @Override
    public Object getItem(int location) {
        return contactoItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.contacto_item, null);

        TextView nombre_contacto = convertView.findViewById(R.id.tv_contacto_nombre);

        contacts = contactoItems.get(position);

        convertView.setOnClickListener(new CustomOnClickListener(callback, position)); // Pass in the callback and the current position

        nombre_contacto.setText(contacts.getNombreContacto());

        return convertView;
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // arg2 = the id of the item in our view (List/Grid) that we clicked
        // arg3 = the id of the item that we have clicked
        // if we didn't assign any id for the Object (Book) the arg3 value is 0
        // That means if we comment, aBookDetail.setBookIsbn(i); arg3 value become 0
        Toast.makeText(context, "You clicked on position : " + arg2 + " and id : " + arg3, Toast.LENGTH_LONG).show();
    }


}
