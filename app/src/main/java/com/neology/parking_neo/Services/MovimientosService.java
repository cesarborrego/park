package com.neology.parking_neo.Services;

import android.os.AsyncTask;

import com.neology.parking_neo.model.Movimientos;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Cesar Segura on 11/04/2017.
 */

public class MovimientosService extends AsyncTask<JSONObject, Void, ArrayList<Movimientos>>{
    @Override
    protected ArrayList<Movimientos> doInBackground(JSONObject... jsonObjects) {
        return null;
    }
}
