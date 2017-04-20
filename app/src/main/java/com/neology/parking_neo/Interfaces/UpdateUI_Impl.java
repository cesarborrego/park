package com.neology.parking_neo.Interfaces;

import android.widget.TextView;

/**
 * Created by Cesar Segura on 18/04/2017.
 */

public class UpdateUI_Impl implements UpdateUI {
    int montoActualizado;

    @Override
    public void setMontoTextView(TextView textView, int iMonto) {

    }

    @Override
    public Integer getMonto() {
        return montoActualizado;
    }

    @Override
    public void setMonto(int montoActualizado) {
        this.montoActualizado = montoActualizado;
    }
}
