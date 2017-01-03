package com.avoupavou.stalker.location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Pantazis on 26-Dec-16.
 */

public class PlaceEntry {

    int size;
    String name;
    LatLng latLon;

    public LatLng getLatLon() {
        return latLon;
    }

    public void setLatLon(LatLng latLon) {
        this.latLon = latLon;
    }

    public PlaceEntry() {

    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
