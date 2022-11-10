package com.example.gps;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
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


public class MapFragment extends Fragment {

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate (R.layout.fragment_map, container, false);

        //initialize fragment
        SupportMapFragment suppportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        //async map
        suppportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                LatLng latLng=new LatLng(57.7089, 11.9746);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        latLng, 13));

                googleMap.addMarker(markerOptions);

    /*            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    //when map is loaded
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        //when clicked om map
                        //Initialize markerOptions
                        MarkerOptions markerOptions = new MarkerOptions();
                        //Set position of Marker
                        markerOptions.position(latLng);
                        //Set title of marker
                        markerOptions.title(latLng.latitude + " : "+ latLng.longitude);
                        //remove all marker
                        googleMap.clear();
                        //Animationg to zoom the marker
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 10
                        ));
                        googleMap.addMarker(markerOptions);
                    }
                });
     */
            }
        });

        return view;
    }
}