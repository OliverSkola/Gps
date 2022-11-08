package com.example.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationUpdate {
    public static final int DEF_UPDATE = 5000;
    public static final int DEF_MIN_UPDATE = 3000;

    public static void Updater(Context context) {


        //Used to find location
        FusedLocationProviderClient locationClient;
        //Settings for FusedLocation
        LocationRequest.Builder builder = new LocationRequest.Builder(DEF_UPDATE);

        builder.setMinUpdateIntervalMillis(DEF_MIN_UPDATE);
        LocationRequest locationReq = builder.build();

        System.out.println("locationReq = " + locationReq.toString());

        locationClient = LocationServices.getFusedLocationProviderClient(context);

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                String[] res = {latitude, longitude};
                String loc = "updated latitude set to: " + res[0] + "updated longitude set to: " + res[1];
                System.out.println(loc);

            }
        };
        autoUpdates(locationClient,locationReq,locationCallback);


/*
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("permissions woooo");

            locationClient.getCurrentLocation(100, null)
                    .addOnSuccessListener((Activity) context, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // If null, check emulator settings
                            if (location != null) {

                                String longitude = String.valueOf(location.getLongitude());
                                String latitude = String.valueOf(location.getLatitude());
                                String[] res = {latitude, longitude};
                                String loc = "latitude set to: " + res[0] + "longitude set to: " + res[1];
                                System.out.println(loc);
                            } else {
                                System.out.println("No location found booo");
                            }

                        }
                    });

        } else {
            System.out.println("no permissions booooo");
        }
*/
        /*
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Ask for permissions
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        */

    }

    @SuppressLint("MissingPermission")
    private static void autoUpdates(FusedLocationProviderClient loccli, LocationRequest locreq, LocationCallback loccal) {

        loccli.requestLocationUpdates(locreq, loccal, null);

    }
}
