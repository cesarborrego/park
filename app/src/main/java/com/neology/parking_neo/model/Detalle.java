package com.neology.parking_neo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Cesar Segura on 20/04/2017.
 */

public class Detalle  implements Parcelable {
    private String titulo;
    private String descripcion;
    private int resImg;

    public Detalle (String titulo,
                      String descripcion,
                      int resImg) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.resImg = resImg;
    }

    protected Detalle(Parcel in) {
        titulo = in.readString();
        descripcion = in.readString();
        resImg = in.readInt();
    }

    public static final Creator<Detalle> CREATOR = new Creator<Detalle>() {
        @Override
        public Detalle createFromParcel(Parcel in) {
            return new Detalle(in);
        }

        @Override
        public Detalle[] newArray(int size) {
            return new Detalle[size];
        }
    };

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getResImg() {
        return resImg;
    }

    public void setResImg(int resImg) {
        this.resImg = resImg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(titulo);
        parcel.writeString(descripcion);
        parcel.writeInt(resImg);
    }
}
