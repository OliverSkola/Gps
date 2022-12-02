package com.example.gps;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link passFragment#newInstance} factory method to
 * create an instance of this fragment.
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
    private ImageButton map_b;

    public void update_Time(int time){
        tid_t.setText(String.valueOf(time));
    }

    public void update_Info(int kilometer_, int tempo_, int medel_, int elevation_, int kalorier_){
        kilometer_t.setText(String.valueOf(kilometer_));
        tempo_t.setText(String.valueOf(tempo_));
        medel_t.setText(String.valueOf(medel_));
        elevation_t.setText(String.valueOf(elevation_));
        kalorier_t.setText(String.valueOf(kalorier_));
    }
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public passFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment passFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static passFragment newInstance(String param1, String param2) {
        passFragment fragment = new passFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
        map_b = view.findViewById(R.id.map_b);

        pause_b.setOnClickListener(v -> {
            pause_b.setVisibility(View.GONE);

            unpause_b.setVisibility(View.VISIBLE);
            stop_b.setVisibility(View.VISIBLE);
        });

        unpause_b.setOnClickListener(v -> {
            pause_b.setVisibility(View.VISIBLE);

            unpause_b.setVisibility(View.GONE);
            stop_b.setVisibility(View.GONE);
        });

        stop_b.setOnClickListener(v -> {
            Intent intent = new Intent(PassActivity.this, resultActivity.class);
            startActivity(intent);
            finish();
        });

        map_b.setOnClickListener(v -> {


        });
        return inflater.inflate(R.layout.fragment_pass, container, false);
    }
}