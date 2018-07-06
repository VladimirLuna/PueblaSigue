package com.vlim.puebla;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class NueveOnceActivity extends AppCompatActivity {

    Spinner spinner_aquiennotifica;
    String[] items = new String[] {"¿A quién desea notificar?", "A mi mamá", "A las rajas poblanas"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueve_once);

        spinner_aquiennotifica = findViewById(R.id.spinner_aquiennotifica);
        spinner_aquiennotifica.setPrompt("¿A quién desea notificar?");

        /*ArrayList<String> opciones_spinner = new ArrayList<String>();
        opciones_spinner.add("A mi mamá");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones_spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_aquiennotifica.setAdapter(adapter);*/


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_aquiennotifica.setAdapter(adapter);

        spinner_aquiennotifica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                items[0] = "Al 911";
                String selectedItem = items[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public void onBackPressed() { }
}
