package com.vlim.puebla;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Medios_1 extends Fragment {
    //Constructor default
    public Medios_1(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageOne = inflater.inflate(R.layout.medios1, container, false);

        //Every you want add some VIEW you need add findViewId with the Inflater

        //Example
        /*
        TextView ExTV = (TextView)PageOne.findViewById(R.id.Something ID)
         */

        return PageOne;
    }
}
