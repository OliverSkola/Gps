package com.example.gps;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

    boolean flag = true;
    boolean is_on = true;
    int timer = 0;

    private boolean mapShown = false;
    private ImageButton map_b;
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
    private passFragment fragment;

    private double totalDistance = 0;

    public GoogleMap locationMap;

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String SHARED_COORDINATES = "sharedPref";

    public double weight;
    public String trainingType;
    private double calories = 0;


    TimerRunnable timerRunnable = new TimerRunnable();
 //   private List<Polyline> pathPoints;
//    private List<LatLng> pathPointsLatLng = new List<LatLng>() {};
 SupportMapFragment mapFrag = new SupportMapFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent inputIntent = getIntent();
        weight = inputIntent.getDoubleExtra("weight",0);
        trainingType = inputIntent.getStringExtra("trainingType");

        setContentView(R.layout.activity_location);

        LocationRequest.Builder builder = new LocationRequest.Builder(DEF_UPDATE);

        builder.setMinUpdateIntervalMillis(DEF_MIN_UPDATE);

        builder.setMaxUpdateAgeMillis(DEF_MAX_AGE);

        builder.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationReq = builder.build();

        locationClient = LocationServices.getFusedLocationProviderClient(LocationPage.this);



        mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        mapFrag.getView().setVisibility(View.INVISIBLE);
        fragment = new passFragment();
        fragment = (passFragment) getSupportFragmentManager()
                .findFragmentById(R.id.passfragment);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                InternetRunnable runnable = new InternetRunnable(location);
                new Thread(runnable).start();


            }
        };
        map_b = findViewById(R.id.map_b);
        map_b.setOnClickListener(v -> {
            fragSwap();

        });
        start_timer();
        autoUpdates();
    }

    public void change_timer(boolean change){
        is_on = change;
    }

    public void start_timer(){
        new Thread(timerRunnable).start();
    }

    class TimerRunnable implements Runnable{

        @Override
        public void run() {
            while(is_on){
                timer = timer+1;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        fragment.update_Time(timer);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void fragSwap(){
        mapShown = !mapShown;
        if(mapShown){
            mapFrag.getView().setVisibility(View.VISIBLE);
            fragment.getView().setVisibility(View.INVISIBLE);
        }else{
            mapFrag.getView().setVisibility(View.INVISIBLE);
            fragment.getView().setVisibility(View.VISIBLE);
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

            double elevation = Elevation.reqElevation(latitude, longitude);

            //Elevation returns -9999 at errors
            if (elevation != -9999) {
                location.setAltitude(elevation);
            } else if(secondLastLocation != null) { //if elevation data fails for some reason, keep same as before
                location.setAltitude(secondLastLocation.getAltitude());
            }
            System.out.println("current elevation = " + location.getAltitude());


            if (secondLastLocation != null && flag) {
                /*
                double angle = Angles.getAngle(location, secondLastLocation);
                System.out.println("current angle = " + angle);*/

                double distance = location.distanceTo(secondLastLocation);
                double elevationDiff = location.getAltitude() - secondLastLocation.getAltitude();

                double hypotenuse = Math.sqrt(Math.pow(distance,2) + Math.pow(elevationDiff,2));
                distances.add(hypotenuse);
                totalDistance += hypotenuse;

                double speedBetween = averageSpeedLastCoordinates(location,secondLastLocation, hypotenuse);
                averageSpeeds.add(speedBetween);
                System.out.println("current speed = " + speedBetween);

                System.out.println("average speed pass = " + averageSpeedPass());
                System.out.println("total distance pass = " + totalDistance);

                double minutes = (location.getElapsedRealtimeNanos() - secondLastLocation.getElapsedRealtimeNanos()) * Math.pow(10,-9)/60;

                calories = calories + CaloriesBurned.getCalories(weight,minutes,speedBetween*3.6,trainingType);

                //required to use in handler
                final double finalTotalDistance = totalDistance;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mapUpdater();
                        fragment.update_Info(totalDistance/1000, speedBetween, averageSpeedPass(), elevation, calories);
                    }
                });
            }

            String[] res = {latitude, longitude};
            String loc = "updated latitude set to: " + res[0] + " updated longitude set to: " + res[1] + " elevation = " + location.getAltitude();
            System.out.println(loc);
            flag = true;

            //Always do this last
            secondLastLocation = location;
        }
    }

    /**
     * Stops the automatic updates
     */
    public void stopUpdates() {
        flag = false;
        locationClient.removeLocationUpdates(locationCallback);
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
    private double averageSpeedLastCoordinates(Location location1, Location location2, double distance) {
        //double distance = location1.distanceTo(location2);

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
        /*
        SharedPreferences coordinates = getSharedPreferences(SHARED_COORDINATES, MODE_PRIVATE);
        String latitude = coordinates.getString("latitude","57.708870");
        String longitude = coordinates.getString("longitude","11.974560");

        locationMap.setMyLocationEnabled(true);
        locationMap.getUiSettings().setMyLocationButtonEnabled(true);
        LatLng latlng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));


        locationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
        */

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
            addLatestPolyline();

        }else{
            System.out.println("location.size still 0");
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
                    .color(Color.rgb(62, 214, 174))
                    .width(15f)
                    .add(prelastLatlng)
                    .add(lastLatlng);
//                    .add(new LatLng(57.7089,11.9746))
//                    .add(new LatLng(57.7072,11.9715))
//                    .add(new LatLng(57.7068,11.9702));

                locationMap.addPolyline(polyOpt);
        }
    }

    public void end_of_past(){
        Intent intent = new Intent(LocationPage.this, resultActivity.class);
        intent.putExtra("calories",calories);
        intent.putExtra("timer",timer);
        intent.putExtra("distance",totalDistance);
        startActivity(intent);
        finish();
    }

}
