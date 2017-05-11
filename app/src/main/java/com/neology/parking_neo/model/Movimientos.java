package com.neology.parking_neo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cesar Segura on 06/04/2017.
 */

public class Movimientos implements Parcelable {
    private String strTarjetaID;
    private long date;
    private int monto;
    private String strTipoMovimiento;
    private byte[] mapa;

    public Movimientos (String strTarjetaID,
                        long date,
                        int monto,
                        String strTipoMovimiento,
                        byte [] mapa) {
        this.setStrTarjetaID(strTarjetaID);
        this.setDate(date);
        this.setMonto(monto);
        this.setStrTipoMovimiento(strTipoMovimiento);
        this.setMapa(mapa);
    }


    protected Movimientos(Parcel in) {
        setStrTarjetaID(in.readString());
        setDate(in.readLong());
        setMonto(in.readInt());
        setStrTipoMovimiento(in.readString());
        setMapa(in.createByteArray());
    }

    public static final Creator<Movimientos> CREATOR = new Creator<Movimientos>() {
        @Override
        public Movimientos createFromParcel(Parcel in) {
            return new Movimientos(in);
        }

        @Override
        public Movimientos[] newArray(int size) {
            return new Movimientos[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getStrTarjetaID());
        parcel.writeLong(getDate());
        parcel.writeInt(getMonto());
        parcel.writeString(getStrTipoMovimiento());
        parcel.writeByteArray(getMapa());
    }

    public String getStrTarjetaID() {
        return strTarjetaID;
    }

    public void setStrTarjetaID(String strTarjetaID) {
        this.strTarjetaID = strTarjetaID;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public String getStrTipoMovimiento() {
        return strTipoMovimiento;
    }

    public void setStrTipoMovimiento(String strTipoMovimiento) {
        this.strTipoMovimiento = strTipoMovimiento;
    }

    public byte[] getMapa() {
        return mapa;
    }

    public void setMapa(byte[] mapa) {
        this.mapa = mapa;
    }
}
