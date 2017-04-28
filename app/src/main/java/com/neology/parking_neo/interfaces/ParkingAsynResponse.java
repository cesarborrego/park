package com.neology.parking_neo.interfaces;

import com.neology.parking_neo.model.Estacionamientos;

import java.util.ArrayList;

/**
 * Created by Cesar Segura on 27/04/2017.
 */

public interface ParkingAsynResponse {
    void processFinish(Boolean output, ArrayList<Estacionamientos> estacionamientosArrayList);
}
