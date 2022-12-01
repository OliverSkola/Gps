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

public class resultActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        final ImageButton back_b = findViewById(R.id.back_b);

        back_b.setOnClickListener(v -> {

            Intent intent = new Intent(resultActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        });
    }
}