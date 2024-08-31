package com.example.fivepointsproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

public class MainActivity extends AppCompatActivity {

    private TextView levelTextView;
    private TextView coinsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        findViewById(R.id.start).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, GameActivity.class)));


        levelTextView = findViewById(R.id.levelTextView);
        displayLevel();

        coinsTextView = findViewById(R.id.coinsTextView);
        Drawable coin = AppCompatResources.getDrawable(this, R.drawable.coin);
        assert coin != null;
        coin.setBounds(0, 0, 100, 100);
        coinsTextView.setCompoundDrawables(null, null, coin, null);
        displayCoins();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayLevel();
        displayCoins();
    }

    private void displayLevel() {
        SharedPreferences sharedPref = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        int level = sharedPref.getInt("Level", 1); // Default level is 1
        String text = String.valueOf(level);
        levelTextView.setText(text);
    }

    private void displayCoins() {
        SharedPreferences sharedPref = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        int coins = sharedPref.getInt("Coins", 0); // Default coin number is 0
        String text = String.valueOf(coins);
        coinsTextView.setText(text);
    }
}
