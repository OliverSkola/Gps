package com.example.gps;

import static android.content.Context.MODE_PRIVATE;

import static com.example.gps.LocationPage.SHARED_COORDINATES;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class OurMapFragment extends Fragment implements OnMapReadyCallback{
    static LatLng latlng2;

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);


        //initialize fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

       // initialize map istället för fragment?

 //       GoogleMap map = supportMapFragment.getMap();


        //async map
        supportMapFragment.getMapAsync(this);

        return view;
    }


    GoogleMap map;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map=googleMap;

        SharedPreferences coordinates = getContext().getSharedPreferences(SHARED_COORDINATES, MODE_PRIVATE);
        String latitude = coordinates.getString("latitude","57.708870");
        String longitude = coordinates.getString("longitude","11.974560");

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        LatLng latlng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));

//        updateLocationUI(latitude,longitude);
//                    getDeviceLocation();

    }


    public  void updateLocationUI(String lat, String lon) {
        if (map == null) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);


        SharedPreferences coordinates = getContext().getSharedPreferences(SHARED_COORDINATES, MODE_PRIVATE);
        String latitude = coordinates.getString("latitude","57.708870");
        String longitude = coordinates.getString("longitude","11.974560");



        System.out.println("\n\nLatitude: "+lat+"   Longitude: "+lon);

        LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));

    }


/* only static
    public static void updateLocationUI() {

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

 */

}