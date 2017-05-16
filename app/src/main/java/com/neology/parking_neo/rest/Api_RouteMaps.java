package com.neology.parking_neo.rest;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.neology.parking_neo.fragments.MapFragment;
import com.neology.parking_neo.interfaces.RouteMapsApiResponse;
import com.neology.parking_neo.utils.MapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Cesar Segura on 27/04/2017.
 */

public class Api_RouteMaps extends AsyncTask<JSONObject, Void, PolylineOptions> {
    private PolylineOptions polylineOptions;
    private RouteMapsApiResponse routeMapsApiResponse;
    String points;

    public Api_RouteMaps(RouteMapsApiResponse routeMapsApiResponse) {
        this.routeMapsApiResponse = routeMapsApiResponse;
    }

    @Override
    protected PolylineOptions doInBackground(JSONObject... response) {
        try {
            if (response[0].has("status")) {
                String status = response[0].getString("status");
                if (status.equals("OK")) {
                    JSONArray jsonArray = response[0].getJSONArray("routes");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    JSONObject overview_polyline = jsonObject.getJSONObject("overview_polyline");
                    points = overview_polyline.getString("points");
                    List<LatLng> listaCoordenadasRuta = MapUtils.decode(points);
                    polylineOptions = new PolylineOptions();
                    polylineOptions.color(Color.argb(150, 0, 181, 247)).width(25);
                    for (LatLng latLng : listaCoordenadasRuta) {
                        polylineOptions.add(latLng);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polylineOptions;
    }

    @Override
    protected void onPostExecute(PolylineOptions polylineOptions) {
        super.onPostExecute(polylineOptions);
        if (polylineOptions != null) {
            routeMapsApiResponse.processFinish(true, polylineOptions, points);
        } else {
            routeMapsApiResponse.processFinish(false, null, null);
        }
    }
}
