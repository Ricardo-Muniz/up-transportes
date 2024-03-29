package com.upbrasil.up.model;

import java.io.Serializable;

public class Localizacao implements Serializable {
    public double latitude;
    public double longitude;

    public Localizacao() {

    }


    public Localizacao(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
