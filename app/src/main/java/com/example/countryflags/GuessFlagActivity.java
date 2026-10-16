package com.example.countryflags;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GuessFlagActivity extends AppCompatActivity {

    private MaterialCardView[] cards = new MaterialCardView[3];
    private ImageView[] images = new ImageView[3];
    private TextView txtTargetCountry, txtResult, txtScore;
    private Button btnNext;

    private ArrayList<FlagModel> flagList;
    private List<FlagModel> roundFlags = new ArrayList<>();
    private List<Integer> previousRoundIndices = new ArrayList<>();
    private int correctCardIndex;
    private int currentScore = 0;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_flag);

        cards[0] = findViewById(R.id.card_flag_1);
        cards[1] = findViewById(R.id.card_flag_2);
        cards[2] = findViewById(R.id.card_flag_3);

        images[0] = findViewById(R.id.img_flag_1);
        images[1] = findViewById(R.id.img_flag_2);
        images[2] = findViewById(R.id.img_flag_3);

        txtTargetCountry = findViewById(R.id.txt_target_country);
        txtResult = findViewById(R.id.txt_result);
        txtScore = findViewById(R.id.txt_score);
        btnNext = findViewById(R.id.btn_next);

        flagList = new ArrayList<>();
        random = new Random();
        loadFlagData(flagList);

        loadNextRound();

        for (int i = 0; i < 3; i++) {
            final int index = i;
            cards[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFlagSelected(index);
                }
            });
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNextRound();
            }
        });
    }

    private void loadNextRound() {
        btnNext.setVisibility(View.GONE);
        txtResult.setText("");

        // Reset visual appearance of cards
        for (MaterialCardView card : cards) {
            card.setEnabled(true);
            card.setCardBackgroundColor(Color.WHITE);
            card.setStrokeColor(Color.parseColor("#E0E0E0"));
            card.setStrokeWidth(2);
        }

        // Pick 3 indices ensuring none overlap with previous round
        List<Integer> availableIndices = new ArrayList<>();
        for (int i = 0; i < flagList.size(); i++) {
            if (!previousRoundIndices.contains(i)) {
                availableIndices.add(i);
            }
        }
        Collections.shuffle(availableIndices);

        List<Integer> currentIndices = availableIndices.subList(0, 3);
        previousRoundIndices = new ArrayList<>(currentIndices);

        roundFlags.clear();
        for (int i = 0; i < 3; i++) {
            FlagModel flag = flagList.get(currentIndices.get(i));
            roundFlags.add(flag);
            images[i].setImageResource(flag.getFlagImageResId());
        }

        correctCardIndex = random.nextInt(3);
        txtTargetCountry.setText(roundFlags.get(correctCardIndex).getCountryName());
    }

    private void onFlagSelected(int clickedIndex) {
        // Disable all 3 cards immediately
        for (MaterialCardView card : cards) {
            card.setEnabled(false);
        }

        // Highlight correct country flag with blue background
        cards[correctCardIndex].setCardBackgroundColor(Color.parseColor("#2196F3"));
        cards[correctCardIndex].setStrokeColor(Color.parseColor("#1976D2"));

        if (clickedIndex == correctCardIndex) {
            currentScore++;
            txtResult.setText("CORRECT!");
            txtResult.setTextColor(Color.GREEN);
        } else {
            txtResult.setText("WRONG!");
            txtResult.setTextColor(Color.RED);
        }

        txtScore.setText("Score: " + currentScore);
        btnNext.setVisibility(View.VISIBLE);
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