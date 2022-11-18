package com.example.gps;

import java.util.LinkedList;

public class SportSession {

    static class Signal{
        double latitude;
        double longitude;

        public Signal(double lat, double lon) {
            latitude=lat;
            longitude=lon;
        }

        public void saveSignal(double lat, double lon){
            latitude=lat;
            longitude=lon;
        }
    }

    static LinkedList<Signal> track = new LinkedList <Signal>();

}
