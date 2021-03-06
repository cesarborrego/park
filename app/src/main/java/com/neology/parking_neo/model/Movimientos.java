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
    private String ruta;
    private double latDest;
    private double lngDest;
    private double latOri;
    private double lngOri;

    public Movimientos(String strTarjetaID,
                       long date,
                       int monto,
                       String strTipoMovimiento,
                       String ruta,
                       double latDes,
                       double lngDes,
                       double latOri,
                       double lngOri) {
        this.setStrTarjetaID(strTarjetaID);
        this.setDate(date);
        this.setMonto(monto);
        this.setStrTipoMovimiento(strTipoMovimiento);
        this.setRuta(ruta);
        this.setLatDest(latDes);
        this.setLngDest(lngDes);
        this.setLatOri(latOri);
        this.setLngOri(lngOri);
    }


    protected Movimientos(Parcel in) {
        setStrTarjetaID(in.readString());
        setDate(in.readLong());
        setMonto(in.readInt());
        setStrTipoMovimiento(in.readString());
        setRuta(in.readString());
        setLatDest(in.readDouble());
        setLngDest(in.readDouble());
        setLatOri(in.readDouble());
        setLngOri(in.readDouble());
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
        parcel.writeString(getRuta());
        parcel.writeDouble(getLatDest());
        parcel.writeDouble(getLngDest());
        parcel.writeDouble(getLatOri());
        parcel.writeDouble(getLngOri());
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

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public double getLatDest() {
        return latDest;
    }

    public void setLatDest(double latDest) {
        this.latDest = latDest;
    }

    public double getLngDest() {
        return lngDest;
    }

    public void setLngDest(double lngDest) {
        this.lngDest = lngDest;
    }

    public double getLatOri() {
        return latOri;
    }

    public void setLatOri(double latOri) {
        this.latOri = latOri;
    }

    public double getLngOri() {
        return lngOri;
    }

    public void setLngOri(double lngOri) {
        this.lngOri = lngOri;
    }
}
