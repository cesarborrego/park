package com.neology.parking_neo.interfaces;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Cesar Segura on 27/04/2017.
 */

public interface RouteMapsApiResponse {
    void processFinish(Boolean output, PolylineOptions polylineOptions, String ruta);
}
