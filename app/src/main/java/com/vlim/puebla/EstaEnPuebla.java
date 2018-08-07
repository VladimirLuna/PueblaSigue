package com.vlim.puebla;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class EstaEnPuebla {
    String TAG = "PUEBLA";
    double latitude, longitude;
    public Context context;

    public EstaEnPuebla(Context applicationContext) {
        this.context = applicationContext;
    }

    public boolean estaEnPuebla(double lat, double lng) {
        LatLng ahorita = new LatLng(lat, lng);
        Log.d(TAG, "Ahorita: " + lat + ", " + lng);

        // Polígono Puebla
        List<LatLng> pts = new ArrayList<>();
        pts.add(new LatLng(19.475111, -98.678410));  // 1
        pts.add(new LatLng(19.013519, -98.682481));  // 2
        pts.add(new LatLng(18.553707, -98.812331));  // 3
        pts.add(new LatLng(18.371879, -99.094661));  // 4
        pts.add(new LatLng(18.178287, -99.024975));  // 5
        pts.add(new LatLng(17.841383, -98.413673));  // 6
        pts.add(new LatLng(17.977748, -97.491530));  // 7
        pts.add(new LatLng(18.342539, -96.705537));  // 8
        pts.add(new LatLng(18.604202, -96.871305));  // 9
        pts.add(new LatLng(18.540893, -97.069911));  // 10
        pts.add(new LatLng(18.711540, -97.294263));  // 11
        pts.add(new LatLng(19.111150, -97.200766));  // 12
        pts.add(new LatLng(19.115001, -97.004913));  // 13
        pts.add(new LatLng(19.314986, -96.975504));  // 14
        pts.add(new LatLng(19.457550, -97.330630));  // 15
        pts.add(new LatLng(20.141368, -97.110482));  // 16
        pts.add(new LatLng(20.262468, -97.444207));  // 17
        pts.add(new LatLng(20.133477, -97.577760));  // 18
        pts.add(new LatLng(20.382623, -97.664632));  // 19
        pts.add(new LatLng(20.491056, -97.531055));  // 20
        pts.add(new LatLng(20.634175, -97.680030));  // 21
        pts.add(new LatLng(20.832998, -97.725546));  // 22
        pts.add(new LatLng(20.854340, -97.888365));  // 23
        pts.add(new LatLng(20.463120, -98.061265));  // 24
        pts.add(new LatLng(19.735090, -98.328288));  // 25
        pts.add(new LatLng(19.338363, -97.689296));  // 26
        pts.add(new LatLng(19.317953, -98.123226));  // 27
        pts.add(new LatLng(19.474765, -98.512821));  // 28

        boolean contains1 = PolyUtil.containsLocation(ahorita.latitude, ahorita.longitude, pts, true);

        if (contains1) {
            return true;
        }
        return false;
    }
}
