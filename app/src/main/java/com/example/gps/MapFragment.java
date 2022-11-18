package com.example.gps;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import static com.example.gps.LocationPage.LATITUDE;
import static com.example.gps.LocationPage.LONGITUDE;
import static com.example.gps.LocationPage.SHARED_COORDINATES;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;


public class MapFragment extends Fragment implements OnMapReadyCallback{
    static LatLng latlng2;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);


        //initialize fragment
        SupportMapFragment suppportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);

        //async map
        suppportMapFragment.getMapAsync(this);

        return view;
    }



           GoogleMap map;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map=googleMap;
        updateLocationUI();
//                    getDeviceLocation();

    }


  private void updateLocationUI() {

      if (map == null) {
          return;
      }
      map.setMyLocationEnabled(true);
      map.getUiSettings().setMyLocationButtonEnabled(true);


      SharedPreferences coordinates = getContext().getSharedPreferences(SHARED_COORDINATES, MODE_PRIVATE);
      String latitude = coordinates.getString("latitude","57.708870");
      String longitude = coordinates.getString("longitude","11.974560");


      LatLng latlng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

      map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
      map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));

  }

}