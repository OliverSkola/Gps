package com.example.gps;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocationPage extends AppCompatActivity implements OnMapReadyCallback {
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

    public GoogleMap locationMap;

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String SHARED_COORDINATES = "sharedPref";

    List<Location> locations = Collections.synchronizedList(new ArrayList<Location>());

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

        SupportMapFragment mapFrag = new SupportMapFragment();

        mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                locations.add(location);
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                String[] res = {latitude, longitude};
                String loc = "updated latitude set to: " + res[0] + " updated longitude set to: " + res[1];
                System.out.println(loc);


                //saving coordinates in Shared Preferences as float
                SharedPreferences sharedPreferences =getSharedPreferences(SHARED_COORDINATES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(LATITUDE, latitude);
                editor.putString(LONGITUDE, longitude);

                editor.apply();

                if(locationMap!=null){
                    System.out.println("it's not null");
                }else{
                    System.out.println("it's still null");
                }

                //map.updateLocationUI(latitude, longitude);
                SportSession.Signal s= new SportSession.Signal(location.getLatitude(), location.getLongitude());
                SportSession.track.addLast(s);

//                System.out.println("\n\nLat: "+SportSession.track.getLast().latitude+" Long: "+SportSession.track.getLast().longitude);
                mapUpdater();
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

                                String longitude = String.valueOf(location.getLongitude());
                                String latitude = String.valueOf(location.getLatitude());
                                String[] res = {latitude, longitude};
                                String loc = "latitude set to: " + res[0] + "longitude set to: " + res[1];
                                System.out.println(loc);
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
