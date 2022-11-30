package com.example.gps;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * @author Ludvig Andersson
 * LocationPage is used to get consistent coordinate updates.
 * Through the class Elevation it also receives altitude data.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LocationPage extends AppCompatActivity implements OnMapReadyCallback {

    public static final int DEF_UPDATE = 5000; //At most 5000ms between updates
    public static final int DEF_MIN_UPDATE = 3000; //At least 3000ms between updates
    public static final int DEF_MAX_AGE = 6000; //When starting after stopping, old data is allowed to at most be 6000ms

    FusedLocationProviderClient locationClient; //Used to find location
    LocationRequest locationReq; //Config for locationClient
    LocationCallback locationCallback; //Used when updating automatically

    Location secondLastLocation = null; //required to calculate distance/speed
    List<Location> locations = Collections.synchronizedList(new ArrayList<Location>());
    List<Double> distances = Collections.synchronizedList(new ArrayList<Double>());
    List<Double> averageSpeeds = Collections.synchronizedList(new ArrayList<Double>());

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private TextView testView;

    private Button autoUpdateStart;
    private Button currentLocation;
    private Button stopLocation;

    private double totalDistance = 0;

    public GoogleMap locationMap;

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String SHARED_COORDINATES = "sharedPref";

    List<Location> locations = Collections.synchronizedList(new ArrayList<Location>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        autoUpdateStart = findViewById(R.id.startAuto);
        currentLocation = findViewById(R.id.locationTest);
        stopLocation = findViewById(R.id.stopAuto);
        testView = findViewById(R.id.textViewTesting);

        LocationRequest.Builder builder = new LocationRequest.Builder(DEF_UPDATE);

        builder.setMinUpdateIntervalMillis(DEF_MIN_UPDATE);

        builder.setMaxUpdateAgeMillis(DEF_MAX_AGE);

        builder.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationReq = builder.build();

        locationClient = LocationServices.getFusedLocationProviderClient(LocationPage.this);

        SupportMapFragment mapFrag = new SupportMapFragment();

        mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                InternetRunnable runnable = new InternetRunnable(location);
                new Thread(runnable).start();

                mapUpdater();
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



         Button button3=findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationPage.this, MapDisplayer.class);
                startActivity(intent);
//                finish();
            }
        });

    }

    /**
     * Required to use Elevation, since internet is not allowed to be used on main thread.
     * Requires a Location for constructor, then uses that Location to find elevation and prints out coordinates and elevation.
     */
    class InternetRunnable implements Runnable {
        Location location;

        InternetRunnable(Location location) {
            this.location = location;
        }

        @Override
        public void run() {
            locations.add(location);
            String longitude = String.valueOf(location.getLongitude());
            String latitude = String.valueOf(location.getLatitude());
            //Temporarily removed code, don't want to waste lookups for elevation for no reason when testing other things
            /*
            //double elevation = Elevation.reqElevation(latitude, longitude);
            //Elevation returns -9999 at errors

            if (elevation != -9999) {
                location.setAltitude(elevation);
            } else if(secondLastLocation != null) { //if elevation data fails for some reason, keep same as before
                location.setAltitude(secondLastLocation.getAltitude());
            }
            double angle = Angles.getAngle(location, secondLastLocation);

            */

            if (secondLastLocation != null) {
                double speedBetween = averageSpeedLastCoordinates(location,secondLastLocation);
                averageSpeeds.add(speedBetween);
                System.out.println("current speed = " + speedBetween);

                double distance = location.distanceTo(secondLastLocation);
                distances.add(distance);
                totalDistance += distance;

                System.out.println("average speed pass = " + averageSpeedPass());
                System.out.println("total distance pass = " + totalDistance);

                //required to use in handler
                final double finalTotalDistance = totalDistance;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        testView.setText("totalDistance = " + finalTotalDistance);
                    }
                });
            }

            String[] res = {latitude, longitude};
            String loc = "updated latitude set to: " + res[0] + " updated longitude set to: " + res[1] + " elevation = " + location.getAltitude();
            System.out.println(loc);


            //Always do this last
            secondLastLocation = location;
        }
    }

    /**
     * Stops the automatic updates
     */
    private void stopUpdates() {
        locationClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Starts automatic updates, to add things to be done during updates see InternetRunnable
     */
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

    /**
     * Calculates the average speed from the time of the first location to the time of the latest location
     * @return Average speed in m/s for the whole pass
     */
    private double averageSpeedPass(){
        double timeDifference = locations.get(locations.size()-1).getElapsedRealtimeNanos() * Math.pow(10, -9) - locations.get(0).getElapsedRealtimeNanos() * Math.pow(10, -9);
        return totalDistance/timeDifference;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        locationMap = googleMap;
        SharedPreferences coordinates = getSharedPreferences(SHARED_COORDINATES, MODE_PRIVATE);
        String latitude = coordinates.getString("latitude","57.708870");
        String longitude = coordinates.getString("longitude","11.974560");

        locationMap.setMyLocationEnabled(true);
        locationMap.getUiSettings().setMyLocationButtonEnabled(true);
        LatLng latlng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        locationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));

    }

    public void mapUpdater(){
        if (locationMap == null) {
            return;
        }
        locationMap.setMyLocationEnabled(true);
        locationMap.getUiSettings().setMyLocationButtonEnabled(true);


        SharedPreferences coordinates = getSharedPreferences(SHARED_COORDINATES, MODE_PRIVATE);
        String latitude = coordinates.getString("latitude","57.708870");
        String longitude = coordinates.getString("longitude","11.974560");




        LatLng latlng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        locationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));

        /*
        locationMap.setMyLocationEnabled(true);
        locationMap.getUiSettings().setMyLocationButtonEnabled(true);
        if(locations.size() > 0){
            Location lastLocation = locations.get(locations.size()-1);
            LatLng latlng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            locationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
            //locationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
        }else{
            System.out.println("location.size still 0");
        }*/
    }
}
