package com.vlim.puebla;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Medios_2 extends Fragment {
    //Constructor default
    public Medios_2(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageTwo = inflater.inflate(R.layout.medios2, container, false);



        return PageTwo;
    }
}
