package com.example.gps;

import android.annotation.SuppressLint;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
/*
Alternativ om static funktion är smidigare, används just nu inte.
Se onCreate i LocationPage för vad variablerna bör innehålla
 */
public class LocationUpdate {

    @SuppressLint("MissingPermission")
    public static void autoUpdates(FusedLocationProviderClient loccli, LocationRequest locreq, LocationCallback loccal) {

        loccli.requestLocationUpdates(locreq, loccal, null);

    }
}
