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
import androidx.fragment.app.FragmentManager;

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

    private Button swapperButton;

    private boolean mapShown = false;

    private double totalDistance = 0;

    private GoogleMap locationMap;
    private SupportMapFragment mapFrag;
    private ButtonFragment buttonFrag;

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String SHARED_COORDINATES = "sharedPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        swapperButton = findViewById(R.id.swapper);

        LocationRequest.Builder builder = new LocationRequest.Builder(DEF_UPDATE);

        builder.setMinUpdateIntervalMillis(DEF_MIN_UPDATE);

        builder.setMaxUpdateAgeMillis(DEF_MAX_AGE);

        builder.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationReq = builder.build();

        locationClient = LocationServices.getFusedLocationProviderClient(LocationPage.this);

        mapFrag = new SupportMapFragment();
        mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        mapFrag.getView().setVisibility(View.INVISIBLE);

        buttonFrag = new ButtonFragment();
        buttonFrag = (ButtonFragment) getSupportFragmentManager()
                .findFragmentById(R.id.buttons);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                InternetRunnable runnable = new InternetRunnable(location);
                new Thread(runnable).start();
            }
        };

        swapperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mapShown){
                    mapShown = false;
                    fragSwap();
                }else{
                    mapShown = true;
                    fragSwap();
                }
            }
        });

    }

    private void fragSwap(){
        if(mapShown){
            mapFrag.getView().setVisibility(View.VISIBLE);
            buttonFrag.getView().setVisibility(View.INVISIBLE);
        }else{
            mapFrag.getView().setVisibility(View.INVISIBLE);
            buttonFrag.getView().setVisibility(View.VISIBLE);
        }
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
            double elevation = Elevation.reqElevation(latitude, longitude);

            //Elevation returns -9999 at errors
            if (elevation != -9999) {
                location.setAltitude(elevation);
            } else if(secondLastLocation != null) { //if elevation data fails for some reason, keep same as before
                location.setAltitude(secondLastLocation.getAltitude());
            }
            System.out.println("current elevation = " + location.getAltitude());
*/

            if (secondLastLocation != null) {
                double angle = Angles.getAngle(location, secondLastLocation);
                System.out.println("current angle = " + angle);
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
                        mapUpdater();
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
    public void stopUpdates() {
        locationClient.removeLocationUpdates(locationCallback);
        System.out.println("training over");
    }

    /**
     * Starts automatic updates, to add things to be done during updates see InternetRunnable
     */
    public void autoUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LocationPage.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationClient.requestLocationUpdates(locationReq, locationCallback, null);
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
    private double averageSpeedPass() {
        double timeDifference = locations.get(locations.size() - 1).getElapsedRealtimeNanos() * Math.pow(10, -9) - locations.get(0).getElapsedRealtimeNanos() * Math.pow(10, -9);
        return totalDistance / timeDifference;
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        locationMap = googleMap;
        locationMap.setMyLocationEnabled(true);
        locationMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    public void mapUpdater(){
        if (locationMap == null) {
            return;
        }
        locationMap.setMyLocationEnabled(true);
        locationMap.getUiSettings().setMyLocationButtonEnabled(true);
        if(locations.size() > 0){
            Location lastLocation = locations.get(locations.size()-1);
            LatLng latlng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            locationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
            //locationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
            //locationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
            addLatestPolyline();
        }else{
            System.out.println("location.size still 0");
        }
    }
    }



 //   void addLatestPolyline(List<Location> locations){
        void addLatestPolyline(){
        if ((locations != null) && (locations.size()>1)) {
            double lat= locations.get(locations.size() - 1).getLatitude();
            double lng= locations.get(locations.size() - 1).getLongitude();
            LatLng lastLatlng= new LatLng(lat, lng);
            double prelat= locations.get(locations.size() - 2).getLatitude();
            double prelng= locations.get(locations.size() - 2).getLongitude();
            LatLng prelastLatlng= new LatLng(prelat, prelng);


            PolylineOptions polyOpt = new PolylineOptions()
                    .color(0x3ED6AE)
                    .width(5)
                    .add(prelastLatlng)
                    .add(lastLatlng);
//                    .add(new LatLng(57.7089,11.9746))
//                    .add(new LatLng(57.7072,11.9715))
//                    .add(new LatLng(57.7068,11.9702));

                locationMap.addPolyline(polyOpt);
        }
    }

}
