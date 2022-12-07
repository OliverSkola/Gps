package com.example.gps;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author Oliver Brottare and Ludvig Andersson
 * @version 1.0
 * @since   2022-12-07
 * This class is used to data during the training pass
 */
public class passFragment extends Fragment {
    private TextView tid_t;
    private TextView kilometer_t;
    private TextView tempo_t;
    private TextView medel_t;
    private TextView elevation_t;
    private TextView kalorier_t;

    private ImageButton pause_b;
    private ImageButton unpause_b;
    private ImageButton stop_b;
    //private ImageButton map_b;

    /**
     * Updates the timer according to the format used.
     * @param time Current time
     */
    public void update_Time(int time){
        String timer = time / 3600 + ":" + String.format("%02d" ,(time / 60) % 60) + ":" + String.format("%02d" , time % 60);
        tid_t.setText(timer);
    }

    /**
     * Updates everything except the timer.
     * @param kilometer_ Total distance ran
     * @param tempo_ Current speed
     * @param medel_ Average speed
     * @param elevation_ Current elevation
     * @param kalorier_ Total Calories burnt
     */
    public void update_Info(double kilometer_, double tempo_, double medel_, double elevation_, double kalorier_){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);


        kilometer_t.setText(String.valueOf(df.format(kilometer_)));
        tempo_t.setText(String.valueOf(df.format(tempo_)));
        medel_t.setText(String.valueOf(df.format(medel_)));
        elevation_t.setText(String.valueOf(df.format(elevation_)));
        kalorier_t.setText(String.valueOf(df.format(kalorier_)));
    }

    /**
     * Create using the saved instance
     * @param savedInstanceState The saved instance
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){}

    }

    /**
     * Finds all the necessary objects in the layout and adds listeners.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pass, container, false);
         tid_t = view.findViewById(R.id.tid_text);
         kilometer_t = view.findViewById(R.id.kilometer_text);
         tempo_t = view.findViewById(R.id.tempo_text);
         medel_t = view.findViewById(R.id.medel_text);
         elevation_t = view.findViewById(R.id.elevation_text);
        kalorier_t = view.findViewById(R.id.kalorier_text);



        pause_b = view.findViewById(R.id.pause_button);
        unpause_b = view.findViewById(R.id.unpause_button);
        stop_b = view.findViewById(R.id.stop_button);
        //map_b = view.findViewById(R.id.map_b);

        pause_b.setOnClickListener(v -> {
            pause_b.setVisibility(View.GONE);

            unpause_b.setVisibility(View.VISIBLE);
            stop_b.setVisibility(View.VISIBLE);
            ((LocationPage) getActivity()).change_timer(false);
            ((LocationPage) getActivity()).stopUpdates();
        });

        unpause_b.setOnClickListener(v -> {
            pause_b.setVisibility(View.VISIBLE);

            unpause_b.setVisibility(View.GONE);
            stop_b.setVisibility(View.GONE);
            ((LocationPage) getActivity()).change_timer(true);
            ((LocationPage) getActivity()).start_timer();
            ((LocationPage) getActivity()).autoUpdates();
        });

        stop_b.setOnClickListener(v -> {
            ((LocationPage) getActivity()).end_of_past();
        });

        /*map_b.setOnClickListener(v -> {
            ((LocationPage) getActivity()).fragSwap();

        });*/
        return view;
    }
}