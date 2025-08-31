package com.example.inventorymanager.UI.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.inventorymanager.R;


public class Settings extends Fragment {

    EditText etThreshold;
    Spinner spinner;
    Button button;
    SharedPreferences sharedPreferences;

    String[] options = {"Once", "Every Minute", "Every 10 Minutes", "Every Hour", "Every Day"};

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        etThreshold = view.findViewById(R.id.etThreshold);
        spinner = view.findViewById(R.id.spinnerFrequency);
        button = view.findViewById(R.id.btnSaveSettings);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, options);
        spinner.setAdapter(adapter);

// Use context from Activity
        sharedPreferences = requireActivity().getSharedPreferences("settings", MODE_PRIVATE);

        int savedThreshold = sharedPreferences.getInt("low_stock_threshold", 0);
        int savedFreq = sharedPreferences.getInt("low_stock_frequency", 0);

        etThreshold.setText(String.valueOf(savedThreshold));
        spinner.setSelection(savedFreq);




        button.setOnClickListener(v -> {
            String thresholdStr = etThreshold.getText().toString().trim();
            if (thresholdStr.isEmpty()) {
                etThreshold.setError("Enter a valid threshold");
                return;
            }

            int threshold = Integer.parseInt(thresholdStr);
            int freq = spinner.getSelectedItemPosition();

            sharedPreferences.edit()
                    .putInt("low_stock_threshold", threshold)
                    .putInt("low_stock_frequency", freq)
                    .apply();

            Toast.makeText(getContext(), "Settings Saved", Toast.LENGTH_SHORT).show();

        });




    return view;
    }



}