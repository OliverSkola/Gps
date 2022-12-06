package com.example.gps;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.gps.databinding.ActivityResultBinding;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

public class resultActivity extends AppCompatActivity {
    private TextView date_t;
    private TextView kalorer_t;
    private TextView tid_t;
    private TextView distans_t;

    public void update_Info(String date_, double kalorer_, String tid_, double distans_){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        date_t.setText(date_);
        String calories = df.format(kalorer_) + " kCal";
        kalorer_t.setText(calories);
        tid_t.setText(tid_);
        String distance = df.format(distans_) + " km";
        distans_t.setText(distance);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        final ImageButton back_b = findViewById(R.id.back_b);
        date_t = findViewById(R.id.datum_text);
        tid_t = findViewById(R.id.tid_text);
        kalorer_t = findViewById(R.id.kalorier_text);
        distans_t = findViewById(R.id.distans_text);

        Intent intentResult = getIntent();

        Double calories = intentResult.getDoubleExtra("calories",0);
        int time = intentResult.getIntExtra("timer",0);
        Double distance = intentResult.getDoubleExtra("distance",0);

        String timer = time / 3600 + ":" + String.format("%02d" ,(time / 60) % 60) + ":" + String.format("%02d" , time % 60);

        LocalDateTime date = LocalDateTime.now();

        int day = date.getDayOfMonth();
        int month  = date.getMonthValue();
        int year = date.getYear();
        String month_s = "Failed_getting_month";
        switch(month){
            case 1:
                month_s = "Januari";
                break;
            case 2:
                month_s = "Februari";
                break;
            case 3:
                month_s = "Mars";
                break;
            case 4:
                month_s = "April";
                break;
            case 5:
                month_s = "Maj";
                break;
            case 6:
                month_s = "Juni";
                break;
            case 7:
                month_s = "Juli";
                break;
            case 8:
                month_s = "Augusti";
                break;
            case 9:
                month_s = "September";
                break;
            case 10:
                month_s = "Oktober";
                break;
            case 11:
                month_s = "November";
                break;
            case 12:
                month_s = "December";

        }
        String dateString = String.valueOf(day) + " " + month_s + " " + String.valueOf(year);

        update_Info(dateString,calories,timer,distance/1000);

        back_b.setOnClickListener(v -> {
            Intent intentSwitch = new Intent(resultActivity.this, StartActivity.class);
            startActivity(intentSwitch);
            finish();
        });
    }
}