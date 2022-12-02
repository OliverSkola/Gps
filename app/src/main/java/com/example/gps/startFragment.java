package com.example.gps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link startFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class startFragment extends Fragment {

    private ImageView walking_c;
    private ImageView running_c;
    private ImageView cycling_c;

    private TextView activity_t;
    private TextView weight_t;
    private TextView vikt_t;
    private TextView start_t;

    private EditText editText;

    private ImageButton walking_b;
    private ImageButton running_b;
    private ImageButton cycling_b;

    private ImageButton activity_b;
    private ImageButton age_b;
    private ImageButton start_b;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    public startFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment startFragman.
     */
    // TODO: Rename and change types and number of parameters
    public static startFragment newInstance(String param1, String param2) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_start, container, false);
        // setContentView(R.layout.activity_start);

        walking_c = view.findViewById(R.id.walking_check);
        running_c = view.findViewById(R.id.running_check);
        cycling_c = view.findViewById(R.id.cycling_check);

        activity_t = view.findViewById(R.id.aktivitet_svar);
        weight_t = view.findViewById(R.id.vikt_svar);
        vikt_t = view.findViewById(R.id.vikt_t);
        start_t = view.findViewById(R.id.start_t);

        editText = view.findViewById(R.id.editText);

        walking_b = view.findViewById(R.id.walking);
        running_b = view.findViewById(R.id.running);
        cycling_b = view.findViewById(R.id.cycling);

        activity_b = view.findViewById(R.id.aktivitet);
        age_b= view.findViewById(R.id.age);
        start_b = view.findViewById(R.id.start);


        final boolean[] click_activity = {true};


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String char_as_string = charSequence.toString();

                if(char_as_string.contains("\n")){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    editText.setText(charSequence.subSequence(0, charSequence.length()-1));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editText.clearFocus();
            }
        });

        age_b.setOnClickListener(v -> {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, 0);
        });

        activity_b.setOnClickListener(v -> {
            if(click_activity[0]){
                start_b.setVisibility(View.GONE);
                age_b.setVisibility(View.GONE);

                editText.setVisibility(View.GONE);

                vikt_t.setVisibility(View.GONE);
                start_t.setVisibility(View.GONE);
                weight_t.setVisibility(View.GONE);

                walking_b.setVisibility(View.VISIBLE);
                running_b.setVisibility(View.VISIBLE);
                cycling_b.setVisibility(View.VISIBLE);

                if(activity_t.getText().toString().compareTo("Gång") == 0){
                    walking_c.setVisibility(View.VISIBLE);

                    running_c.setVisibility(View.GONE);
                    cycling_c.setVisibility(View.GONE);
                }
                else if(activity_t.getText().toString().compareTo("Löpning") == 0){
                    running_c.setVisibility(View.VISIBLE);

                    walking_c.setVisibility(View.GONE);
                    cycling_c.setVisibility(View.GONE);
                }
                else if(activity_t.getText().toString().compareTo("Cykling") == 0){
                    cycling_c.setVisibility(View.VISIBLE);

                    running_c.setVisibility(View.GONE);
                    walking_c.setVisibility(View.GONE);
                }

                click_activity[0] = false;
            }
            else{
                start_b.setVisibility(View.VISIBLE);
                age_b.setVisibility(View.VISIBLE);

                editText.setVisibility(View.VISIBLE);

                vikt_t.setVisibility(View.VISIBLE);
                start_t.setVisibility(View.VISIBLE);
                weight_t.setVisibility(View.VISIBLE);

                walking_b.setVisibility(View.GONE);
                running_b.setVisibility(View.GONE);
                cycling_b.setVisibility(View.GONE);

                walking_c.setVisibility(View.GONE);
                running_c.setVisibility(View.GONE);
                cycling_c.setVisibility(View.GONE);

                click_activity[0] =true;
            }

        });

        walking_b.setOnClickListener(v -> {
            activity_t.setText("Gång");

            walking_c.setVisibility(View.VISIBLE);

            running_c.setVisibility(View.GONE);
            cycling_c.setVisibility(View.GONE);
        });

        running_b.setOnClickListener(v -> {
            activity_t.setText("Löpning");

            running_c.setVisibility(View.VISIBLE);

            walking_c.setVisibility(View.GONE);
            cycling_c.setVisibility(View.GONE);
        });

        cycling_b.setOnClickListener(v -> {
            activity_t.setText("Cykling");

            cycling_c.setVisibility(View.VISIBLE);

            running_c.setVisibility(View.GONE);
            walking_c.setVisibility(View.GONE);
        });

        start_b.setOnClickListener(v -> {
            if (activity_t.getText().length() != 0 && (editText.getText().length() != 0 && editText.getText().length() < 4)){
                Intent intent = new Intent(StartActivity.this, PassActivity.class);
                startActivity(intent);
                finish();
            }

        });
        return inflater.inflate(R.layout.fragment_start_fragman, container, false);
    }
}