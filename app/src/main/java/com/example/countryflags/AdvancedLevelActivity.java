package com.example.countryflags;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdvancedLevelActivity extends AppCompatActivity {

    private ImageView img1, img2, img3;
    private EditText et1, et2, et3;
    private TextView ans1, ans2, ans3, txtResult, txtScore;
    private Button btnAction;

    private ArrayList<FlagModel> flagList;
    private List<FlagModel> roundFlags = new ArrayList<>();
    private List<Integer> previousIndices = new ArrayList<>();

    private boolean[] isAlreadyCorrect = new boolean[3];
    private int unsuccessfulAttempts = 0;
    private int currentScore = 0;
    private boolean isRoundOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_level);

        img1 = findViewById(R.id.img_flag_1);
        img2 = findViewById(R.id.img_flag_2);
        img3 = findViewById(R.id.img_flag_3);

        et1 = findViewById(R.id.et_country_1);
        et2 = findViewById(R.id.et_country_2);
        et3 = findViewById(R.id.et_country_3);

        ans1 = findViewById(R.id.txt_answer_1);
        ans2 = findViewById(R.id.txt_answer_2);
        ans3 = findViewById(R.id.txt_answer_3);

        txtResult = findViewById(R.id.txt_result);
        txtScore = findViewById(R.id.txt_score);
        btnAction = findViewById(R.id.btn_action);

        flagList = new ArrayList<>();
        loadFlagData(flagList);

        loadNextRound();

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRoundOver) {
                    loadNextRound();
                } else {
                    checkSubmission();
                }
            }
        });
    }

    private void loadNextRound() {
        isRoundOver = false;
        unsuccessfulAttempts = 0;
        isAlreadyCorrect = new boolean[3];
        txtResult.setText("");
        btnAction.setText("Submit");

        resetInputField(et1, ans1);
        resetInputField(et2, ans2);
        resetInputField(et3, ans3);

        List<Integer> available = new ArrayList<>();
        for (int i = 0; i < flagList.size(); i++) {
            if (!previousIndices.contains(i)) {
                available.add(i);
            }
        }
        Collections.shuffle(available);

        List<Integer> selected = available.subList(0, 3);
        previousIndices = new ArrayList<>(selected);

        roundFlags.clear();
        for (int idx : selected) {
            roundFlags.add(flagList.get(idx));
        }

        img1.setImageResource(roundFlags.get(0).getFlagImageResId());
        img2.setImageResource(roundFlags.get(1).getFlagImageResId());
        img3.setImageResource(roundFlags.get(2).getFlagImageResId());
    }

    private void checkSubmission() {
        boolean roundSuccessfulThisTurn = true;

        roundSuccessfulThisTurn &= evaluateField(0, et1);
        roundSuccessfulThisTurn &= evaluateField(1, et2);
        roundSuccessfulThisTurn &= evaluateField(2, et3);

        // Win condition: all 3 identified
        if (isAlreadyCorrect[0] && isAlreadyCorrect[1] && isAlreadyCorrect[2]) {
            txtResult.setText("CORRECT!");
            txtResult.setTextColor(Color.GREEN);
            finishRound();
            return;
        }

        if (!roundSuccessfulThisTurn) {
            unsuccessfulAttempts++;
        }

        // Loss condition: 3 unsuccessful attempts reached
        if (unsuccessfulAttempts >= 3) {
            txtResult.setText("WRONG!");
            txtResult.setTextColor(Color.RED);

            revealAnswer(0, ans1);
            revealAnswer(1, ans2);
            revealAnswer(2, ans3);

            finishRound();
        }
    }

    private boolean evaluateField(int index, EditText editText) {
        if (isAlreadyCorrect[index]) {
            return true; // Already verified and scored
        }

        String userGuess = editText.getText().toString().trim();
        String actualName = roundFlags.get(index).getCountryName().trim();

        if (userGuess.equalsIgnoreCase(actualName)) {
            isAlreadyCorrect[index] = true;
            editText.setTextColor(Color.GREEN);
            editText.setEnabled(false);
            currentScore++; // Award 1 point per correctly identified country
            txtScore.setText("Score: " + currentScore);
            return true;
        } else {
            editText.setTextColor(Color.RED);
            return false;
        }
    }

    private void revealAnswer(int index, TextView ansText) {
        if (!isAlreadyCorrect[index]) {
            ansText.setText("Correct: " + roundFlags.get(index).getCountryName());
            ansText.setTextColor(Color.parseColor("#2196F3"));
            ansText.setVisibility(View.VISIBLE);
        }
    }

    private void finishRound() {
        isRoundOver = true;
        et1.setEnabled(false);
        et2.setEnabled(false);
        et3.setEnabled(false);
        btnAction.setText("Next");
    }

    private void resetInputField(EditText et, TextView ans) {
        et.setText("");
        et.setTextColor(Color.BLACK);
        et.setEnabled(true);
        ans.setVisibility(View.GONE);
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