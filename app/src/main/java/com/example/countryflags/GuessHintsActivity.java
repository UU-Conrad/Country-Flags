package com.example.countryflags;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class GuessHintsActivity extends AppCompatActivity {

    private ImageView imgFlag;
    private TextView txtDashes, txtAttempts, txtResult, txtCorrectCountry, txtScore;
    private EditText etCharacter;
    private Button btnAction;

    private ArrayList<FlagModel> flagList;
    private Random random;
    private int currentPos = -1;
    private int previousPos = -1;
    private int currentScore = 0;
    private int incorrectGuesses = 0;
    private StringBuilder dashedName;
    private boolean isRoundFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_hints);

        imgFlag = findViewById(R.id.img_flag);
        txtDashes = findViewById(R.id.txt_dashes);
        txtAttempts = findViewById(R.id.txt_attempts);
        txtResult = findViewById(R.id.txt_result);
        txtCorrectCountry = findViewById(R.id.txt_correct_country);
        txtScore = findViewById(R.id.txt_score);
        etCharacter = findViewById(R.id.et_character);
        btnAction = findViewById(R.id.btn_action);

        flagList = new ArrayList<>();
        random = new Random();
        loadFlagData(flagList);

        loadNextRound();

        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRoundFinished) {
                    loadNextRound();
                } else {
                    processCharacterGuess();
                }
            }
        });
    }

    private void loadNextRound() {
        isRoundFinished = false;
        incorrectGuesses = 0;
        txtAttempts.setText("Incorrect attempts: 0 / 3");
        txtResult.setText("");
        txtCorrectCountry.setVisibility(View.GONE);
        btnAction.setText("Submit");
        etCharacter.setEnabled(true);
        etCharacter.setText("");

        do {
            currentPos = random.nextInt(flagList.size());
        } while (currentPos == previousPos && flagList.size() > 1);

        previousPos = currentPos;
        FlagModel target = flagList.get(currentPos);
        imgFlag.setImageResource(target.getFlagImageResId());

        // Build initial dashes (spaces kept as spaces)
        dashedName = new StringBuilder();
        for (char c : target.getCountryName().toCharArray()) {
            if (c == ' ') {
                dashedName.append(' ');
            } else {
                dashedName.append('-');
            }
        }
        txtDashes.setText(dashedName.toString());
    }

    private void processCharacterGuess() {
        String input = etCharacter.getText().toString().trim();
        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter a letter", Toast.LENGTH_SHORT).show();
            return;
        }

        char guessChar = Character.toLowerCase(input.charAt(0));
        etCharacter.setText("");
        String answer = flagList.get(currentPos).getCountryName();
        boolean foundMatch = false;

        for (int i = 0; i < answer.length(); i++) {
            if (Character.toLowerCase(answer.charAt(i)) == guessChar) {
                dashedName.setCharAt(i, answer.charAt(i));
                foundMatch = true;
            }
        }

        txtDashes.setText(dashedName.toString());

        if (!foundMatch) {
            incorrectGuesses++;
            txtAttempts.setText("Incorrect attempts: " + incorrectGuesses + " / 3");
        }

        // Win: all letters revealed
        if (!dashedName.toString().contains("-")) {
            endRound(true);
        } else if (incorrectGuesses >= 3) { // Loss: 3 incorrect guesses
            endRound(false);
        }
    }

    private void endRound(boolean hasWon) {
        isRoundFinished = true;
        etCharacter.setEnabled(false);
        btnAction.setText("Next");

        if (hasWon) {
            currentScore++;
            txtResult.setText("CORRECT!");
            txtResult.setTextColor(Color.GREEN);
        } else {
            txtResult.setText("WRONG!");
            txtResult.setTextColor(Color.RED);
            txtCorrectCountry.setText("Correct: " + flagList.get(currentPos).getCountryName());
            txtCorrectCountry.setTextColor(Color.parseColor("#2196F3"));
            txtCorrectCountry.setVisibility(View.VISIBLE);
        }

        txtScore.setText("Score: " + currentScore);
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