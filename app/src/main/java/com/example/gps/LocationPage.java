package com.example.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class LocationPage extends AppCompatActivity {
    //Högst 5000ms mellan uppdateringar
    public static final int DEF_UPDATE = 5000;
    //Minst 3000ms mellan uppdateringar
    public static final int DEF_MIN_UPDATE = 3000;
    //Hittar plats
    FusedLocationProviderClient locationClient;
    //Config för locationClient
    LocationRequest locationReq;
    //Används vid automatisk update
    LocationCallback locationCallback;

    Location secondLastLocation = null;

    List<Location> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Button autoUpdateStart = findViewById(R.id.button);
        Button currentLocation = findViewById(R.id.button2);

        LocationRequest.Builder builder = new LocationRequest.Builder(DEF_UPDATE);

        builder.setMinUpdateIntervalMillis(DEF_MIN_UPDATE);

        builder.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationReq = builder.build();

        locationClient = LocationServices.getFusedLocationProviderClient(LocationPage.this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                //locations.add(location);
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                String[] res = {latitude, longitude};
                String loc = "updated latitude set to: " + res[0] + " updated longitude set to: " + res[1]  + "locationsize:" + locations.size();
                System.out.println(loc);
                //should only be able to calculate average speed if there are two points
                if(locations.size() > 1) {
                    System.out.println("hastighet " + averageSpeedLastCoordinates(secondLastLocation, location));
                }
            }
        };

        autoUpdateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoUpdates();
            }
        });

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstLocation();
            }
        });


    }

    private void autoUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LocationPage.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationClient.requestLocationUpdates(locationReq, locationCallback, null);

    }

    private void firstLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationClient.getCurrentLocation(100, null)
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                //adds secondlast if there are more than one point
                                if(locations.size() > 1){
                                    secondLastLocation = locations.get(locations.size() - 1);
                                }

                                String longitude = String.valueOf(location.getLongitude());
                                String latitude = String.valueOf(location.getLatitude());
                                String[] res = {latitude, longitude};
                                String loc = "latitude set to: " + res[0] + "longitude set to: " + res[1] + " locationsize" + locations.size();
                                System.out.println(loc);

                                //adds current location to list
                                locations.add(location);

                            } else {
                                System.out.println("No location found, set in emulator");
                            }
                        }
                    });

        } else {
            ActivityCompat.requestPermissions(LocationPage.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            System.out.println("no permissions");
        }
    }

    //calculates average speed between two points
    private float averageSpeedLastCoordinates(Location location1, Location location2){
        float[] results = new float[1];
        Location.distanceBetween(location1.getLatitude(), location1.getLongitude(), location2.getLatitude(), location2.getLongitude(), results);

        return (float) ((results[results.length - 1]) / ((location2.getElapsedRealtimeNanos() * Math.pow(10, 9) - location1.getElapsedRealtimeNanos() * Math.pow(10, 9))));
    }
}
