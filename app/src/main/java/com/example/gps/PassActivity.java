package com.example.gps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class PassActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        TextView tid_t = findViewById(R.id.tid_text);
        TextView kilometer_t = findViewById(R.id.kilometer_text);
        TextView tempo_t = findViewById(R.id.tempo_text);
        TextView medel_t = findViewById(R.id.medel_text);
        TextView elevation_t = findViewById(R.id.elevation_text);
        TextView kalorier_t = findViewById(R.id.kalorier_text);



        pause_b = findViewById(R.id.pause_button);
        unpause_b = findViewById(R.id.unpause_button);
        stop_b = findViewById(R.id.stop_button);
        map_b = findViewById(R.id.map_b);

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
    }
}