package com.neology.parking_neo.rest;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.neology.parking_neo.interfaces.ParkingAsynResponse;
import com.neology.parking_neo.model.Estacionamientos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Cesar Segura on 26/04/2017.
 */

public class Api_Parking extends AsyncTask<JSONObject, Void, ArrayList<Estacionamientos>> {

    private ArrayList<Estacionamientos> estacionamientosArrayList;
    private ParkingAsynResponse asynResponse = null;

    public Api_Parking(ParkingAsynResponse asynResponse) {
        this.asynResponse = asynResponse;
    }

    @Override
    protected ArrayList<Estacionamientos> doInBackground(JSONObject... voids) {
        estacionamientosArrayList = new ArrayList<Estacionamientos>();
        try {
            JSONArray jsonArray = voids[0].getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject jsonObject1 = jsonObject.getJSONObject("geometry");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("location");
                Estacionamientos estacionamientos = new Estacionamientos(
                        new LatLng(jsonObject2.getDouble("lat"), jsonObject2.getDouble("lng")),
                        jsonObject.getString("name"),
                        jsonObject.getString("vicinity"));
                estacionamientosArrayList.add(estacionamientos);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return estacionamientosArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<Estacionamientos> estacionamientosArrayList) {
        super.onPostExecute(estacionamientosArrayList);
        if (estacionamientosArrayList.size() > 0) {
            asynResponse.processFinish(true, estacionamientosArrayList);
        } else {
            asynResponse.processFinish(false, null);
        }
    }

}
