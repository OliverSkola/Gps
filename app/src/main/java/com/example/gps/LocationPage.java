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
import com.google.android.gms.tasks.OnSuccessListener;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Button button = findViewById(R.id.button);

        LocationRequest.Builder builder = new LocationRequest.Builder(DEF_UPDATE);

        builder.setMinUpdateIntervalMillis(DEF_MIN_UPDATE);
        locationReq = builder.build();

        System.out.println("locationReq = " + locationReq.toString());

        locationClient = LocationServices.getFusedLocationProviderClient(LocationPage.this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                String[] res = {latitude, longitude};
                String loc = "updated latitude set to: " + res[0] + "updated longitude set to: " + res[1];
                System.out.println(locationResult.getLocations());

            }
        };

        firstLocation();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoUpdates();
            }
        });
    }

    private void autoUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationClient.requestLocationUpdates(locationReq, locationCallback, null);

    }

    private void firstLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            System.out.println("permissions woooo");

            locationClient.getCurrentLocation(100, null)
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
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
            ActivityCompat.requestPermissions(LocationPage.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            System.out.println("no permissions booooo");
        }
    }

}
