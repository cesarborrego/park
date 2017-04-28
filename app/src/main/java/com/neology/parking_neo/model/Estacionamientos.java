package com.neology.parking_neo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Cesar Segura on 26/04/2017.
 */

public class Estacionamientos implements Parcelable{
    private LatLng ubicacion;
    private String nombre;
    private String direccion;
    private double rating;
    private boolean isOpen;

    public Estacionamientos (LatLng ubicacion,
                             String nombre,
                             String direccion) {
        this.ubicacion = ubicacion;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public Estacionamientos (LatLng ubicacion,
                             String nombre,
                             String direccion,
                             double rating,
                             boolean isOpen) {
        this.ubicacion = ubicacion;
        this.nombre = nombre;
        this.direccion = direccion;
        this.rating = rating;
        this.isOpen = isOpen;
    }


    protected Estacionamientos(Parcel in) {
        ubicacion = in.readParcelable(LatLng.class.getClassLoader());
        nombre = in.readString();
        direccion = in.readString();
        rating = in.readDouble();
        isOpen = in.readByte() != 0;
    }

    public static final Creator<Estacionamientos> CREATOR = new Creator<Estacionamientos>() {
        @Override
        public Estacionamientos createFromParcel(Parcel in) {
            return new Estacionamientos(in);
        }

        @Override
        public Estacionamientos[] newArray(int size) {
            return new Estacionamientos[size];
        }
    };

    public LatLng getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(LatLng ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(ubicacion, i);
        parcel.writeString(nombre);
        parcel.writeString(direccion);
        parcel.writeDouble(rating);
        parcel.writeByte((byte) (isOpen ? 1 : 0));
    }
}
