package com.neology.parking_neo.Interfaces;

import android.widget.TextView;

/**
 * Created by Cesar Segura on 17/04/2017.
 */

public interface UpdateUI {
    void setMontoTextView(TextView textView, int iMonto);
    Integer getMonto();
    void setMonto(int montoActualizado);
}
