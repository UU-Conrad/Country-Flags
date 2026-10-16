package com.example.countryflags;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GuessCountryActivity extends AppCompatActivity {

    private ImageView imgFlag;
    private Spinner spinnerCountries;
    private TextView txtResult, txtCorrectCountry, txtScore;
    private Button btnAction;

    private ArrayList<FlagModel> flagList;
    private Random random;
    private int currentPos = -1;
    private int previousPos = -1;
    private int currentScore = 0;
    private boolean isSubmitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_country);

        imgFlag = findViewById(R.id.img_flag);
        spinnerCountries = findViewById(R.id.spinner_countries);
        txtResult = findViewById(R.id.txt_result);
        txtCorrectCountry = findViewById(R.id.txt_correct_country);
        txtScore = findViewById(R.id.txt_score);
        btnAction = findViewById(R.id.btn_action);

        flagList = new ArrayList<>();
        random = new Random();
        loadFlagData(flagList);

        setupCountrySpinner();
        loadNextRound();

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSubmitted) {
                    checkAnswer();
                } else {
                    loadNextRound();
                }
            }
        });
    }

    private void loadNextRound() {
        isSubmitted = false;
        txtResult.setText("");
        txtCorrectCountry.setVisibility(View.GONE);
        btnAction.setText("Submit");
        spinnerCountries.setEnabled(true);

        // Prevent the same flag appearing in consecutive rounds
        do {
            currentPos = random.nextInt(flagList.size());
        } while (currentPos == previousPos && flagList.size() > 1);

        previousPos = currentPos;
        imgFlag.setImageResource(flagList.get(currentPos).getFlagImageResId());
    }

    private void checkAnswer() {
        isSubmitted = true;
        spinnerCountries.setEnabled(false);
        btnAction.setText("Next");

        String selectedCountry = spinnerCountries.getSelectedItem().toString().trim();
        String correctCountry = flagList.get(currentPos).getCountryName().trim();

        // Case-insensitive check matching your Quiz_array logic
        if (selectedCountry.equalsIgnoreCase(correctCountry)) {
            currentScore++;
            txtResult.setText("CORRECT!");
            txtResult.setTextColor(Color.GREEN);
        } else {
            txtResult.setText("WRONG!");
            txtResult.setTextColor(Color.RED);
            txtCorrectCountry.setText("Correct: " + correctCountry);
            txtCorrectCountry.setTextColor(Color.parseColor("#2196F3"));
            txtCorrectCountry.setVisibility(View.VISIBLE);
        }

        txtScore.setText("Score: " + currentScore);
    }

    private void setupCountrySpinner() {
        ArrayList<String> names = new ArrayList<>();
        for (FlagModel item : flagList) {
            names.add(item.getCountryName());
        }
        Collections.sort(names);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountries.setAdapter(adapter);
    }

    private void loadFlagData(ArrayList<FlagModel> list) {
        String[] countryNames = getResources().getStringArray(R.array.country_names);
        for (String name : countryNames) {
            String resName = name.toLowerCase().replace(" ", "_");
            int resId = getResources().getIdentifier(resName, "drawable", getPackageName());
            list.add(new FlagModel(name, resId));
        }
    }
}