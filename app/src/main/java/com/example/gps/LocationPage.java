package com.example.gps;

import android.Manifest;
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

/**
 * @author Ludvig Andersson
 * LocationPage is used to get consistent coordinate updates.
 * Through the class Elevation it also receives altitude data.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocationPage extends AppCompatActivity {
    //At most 5000ms between updates
    public static final int DEF_UPDATE = 5000;
    //At least 3000ms between updates
    public static final int DEF_MIN_UPDATE = 3000;
    //When starting after stopping, old data is allowed to at most be 6000ms
    public static final int DEF_MAX_AGE = 6000;
    //Used to find location
    FusedLocationProviderClient locationClient;
    //Config for locationClient
    LocationRequest locationReq;
    //Used when updating automatically
    LocationCallback locationCallback;

    Location secondLastLocation = null;

    List<Location> locations = Collections.synchronizedList(new ArrayList<Location>());

    List<Double> distances = Collections.synchronizedList(new ArrayList<Double>());

    List<Double> averageSpeeds = Collections.synchronizedList(new ArrayList<Double>());

    /**
     * Required to use Elevation, since internet is not allowed to be used on main thread.
     * Requires a Location for constructor, the uses that Location to find elevation and prints out coordinates and elevation.
     */
    class InternetRunnable implements Runnable {
        Location location;

        InternetRunnable(Location location) {
            this.location = location;
        }

        @Override
        public void run() {
            String longitude = String.valueOf(location.getLongitude());
            String latitude = String.valueOf(location.getLatitude());
            //double elevation = Elevation.reqElevation(latitude, longitude);
            //Elevation returns -9999 at errors
            /*
            if (elevation != -9999) {
                location.setAltitude(elevation);
            } else if(secondLastLocation != null) {
                location.setAltitude(secondLastLocation.getAltitude());
            }
            */
            if (secondLastLocation != null) {
                double speedBetween = averageSpeedLastCoordinates(location,secondLastLocation);
                averageSpeeds.add(speedBetween);
                System.out.println("speed = " + speedBetween);
                double distance = location.distanceTo(secondLastLocation);
                distances.add(distance);
                double totalDistance = 0;
                for (int i = 0; i < distances.size(); i++) {
                    totalDistance = distances.get(i);
                }
                double averageSpeedPass = 0;
                for(int i = 0; i < averageSpeeds.size(); i++){
                    averageSpeedPass += averageSpeeds.get(i);
                }
                System.out.println("average speed pass = " + averageSpeedPass);
                System.out.println("total distance pass = " + totalDistance);
            }
            locations.add(location);
            String[] res = {latitude, longitude};
            String loc = "updated latitude set to: " + res[0] + " updated longitude set to: " + res[1] + " elevation = " + location.getAltitude();
            System.out.println(loc);

            secondLastLocation = location;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Button autoUpdateStart = findViewById(R.id.button);
        Button currentLocation = findViewById(R.id.button2);
        Button stopLocation = findViewById(R.id.stopAuto);

        LocationRequest.Builder builder = new LocationRequest.Builder(DEF_UPDATE);

        builder.setMinUpdateIntervalMillis(DEF_MIN_UPDATE);

        builder.setMaxUpdateAgeMillis(DEF_MAX_AGE);

        builder.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationReq = builder.build();

        locationClient = LocationServices.getFusedLocationProviderClient(LocationPage.this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                InternetRunnable runnable = new InternetRunnable(location);
                new Thread(runnable).start();

                /*
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                String[] res = {latitude, longitude};
                String loc = "updated latitude set to: " + res[0] + " updated longitude set to: " + res[1]  + "locationsize:" + locations.size();
                System.out.println(loc);
                //should only be able to calculate average speed if there are two points
                if(locations.size() > 1) {
                    System.out.println("hastighet " + averageSpeedLastCoordinates(secondLastLocation, location));
                }
                 */
            }
        };

        autoUpdateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoUpdates();
            }
        });

        stopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopUpdates();
            }
        });

        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstLocation();
            }
        });


    }

    private void stopUpdates() {
        locationClient.removeLocationUpdates(locationCallback);
    }

    private void autoUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LocationPage.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationClient.requestLocationUpdates(locationReq, locationCallback, null);

    }

    /**
     * Strictly for testing gps, has no practical use
     */
    private void firstLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationClient.getCurrentLocation(100, null)
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                //adds secondlast if there are more than one point
                                if (locations.size() > 1) {
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
    private double averageSpeedLastCoordinates(Location location1, Location location2) {
        double distance = location1.distanceTo(location2);

        return (double) ((distance) / ((location1.getElapsedRealtimeNanos() * Math.pow(10, -9) - location2.getElapsedRealtimeNanos() * Math.pow(10, -9))));
    }
}
