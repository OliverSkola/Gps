package com.example.gps;

public class Signal {

    double latitude;
    double longitude;

    public Signal(double lat, double lon) {
        this.latitude=lat;
        this.longitude= lon;
    }

    public void saveSignal(double lat, double lon){
        latitude=lat;
        longitude=lon;
    }
}
