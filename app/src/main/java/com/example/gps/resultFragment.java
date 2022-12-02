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
 * Use the {@link resultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class resultFragment extends Fragment {

    private TextView date_t;
    private TextView kalorer_t;
    private TextView tid_t;
    private TextView distans_t;

    public void update_Info(String date_, int kalorer_, int tid_, int distans_){
        date_t.setText(date_);
        kalorer_t.setText(String.valueOf(kalorer_));
        tid_t.setText(String.valueOf(tid_));
        distans_t.setText(String.valueOf(distans_));
    }

    public resultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment resultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static resultFragment newInstance(String param1, String param2) {
        resultFragment fragment = new resultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_result, container, false);


            final ImageButton back_b = view.findViewById(R.id.back_b);

            back_b.setOnClickListener(v -> {

                Intent intent = new Intent(resultActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            });

        return inflater.inflate(R.layout.fragment_result, container, false);
    }
}