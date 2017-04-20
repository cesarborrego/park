package com.neology.parking_neo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cesar Segura on 06/04/2017.
 */

public class Movimientos implements Parcelable{
    private String strTarjetaID;
    private long date;
    private int monto;
    private String strTipoMovimiento;

    public Movimientos (String strTarjetaID,
                        long date,
                        int monto,
                        String strTipoMovimiento) {
        this.setStrTarjetaID(strTarjetaID);
        this.setDate(date);
        this.setMonto(monto);
        this.setStrTipoMovimiento(strTipoMovimiento);
    }


    protected Movimientos(Parcel in) {
        strTarjetaID = in.readString();
        date = in.readLong();
        monto = in.readInt();
        strTipoMovimiento = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(strTarjetaID);
        parcel.writeLong(date);
        parcel.writeInt(monto);
        parcel.writeString(strTipoMovimiento);
    }
}
