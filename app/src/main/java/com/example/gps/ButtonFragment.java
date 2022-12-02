package com.example.gps;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 */
public class ButtonFragment extends Fragment {

    private Button autoUpdateStart;
    private Button currentLocation;
    private Button stopLocation;

    public ButtonFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buttons, container, false);
        autoUpdateStart = view.findViewById(R.id.startAuto);

        autoUpdateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LocationPage) getActivity()).autoUpdates();
            }
        });

        stopLocation = view.findViewById(R.id.stopAuto);

        stopLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LocationPage) getActivity()).stopUpdates();
            }
        });

        return view;
    }
}