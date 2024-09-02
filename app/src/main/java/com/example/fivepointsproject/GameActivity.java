package com.example.fivepointsproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    private Rect bounds;
    private double coinMultiplier;
    private int highscore;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        bounds = getWindowManager().getCurrentWindowMetrics().getBounds();

        sharedPref = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        highscore = sharedPref.getInt("Highscore", 0);
        RelativeLayout layout = new RelativeLayout(this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        params.width = bounds.width();
        params.height = bounds.height();

        gameView = new GameView(this, bounds.width(), bounds.height());
        gameView.levelNum = sharedPref.getInt("Level", 1); // Default level is 1
        gameView.shooterSpeedLvl = sharedPref.getInt("ShooterSpeedLevel", 1); // Default level is 1
        gameView.shooterPowerLvl = sharedPref.getInt("ShooterPowerLevel", 1); // Default level is 1

        layout.addView(gameView, params);

        coinMultiplier = 0.9 + 0.1 * sharedPref.getInt("CoinMultiplierLevel", 1); // Default level is 0

        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        btnParams.leftMargin = 100;
        btnParams.topMargin = 125;
        btnParams.height = 150;
        btnParams.width = 150;

        Button resumeBtn = new Button(this);
        resumeBtn.setBackgroundColor(getColor(R.color.pause_background));
        resumeBtn.setText(R.string.resume_text);
        resumeBtn.setTextColor(getColor(R.color.black));
        resumeBtn.setOnClickListener(
            v -> {
                gameView.resume();
                layout.removeView(resumeBtn);
            }
        );

        Button pauseBtn = new Button(this);
        pauseBtn.setBackground(getDrawable(R.drawable.pause));
        pauseBtn.setOnClickListener(
            v -> {
                gameView.pause();
                layout.addView(resumeBtn, bounds.width(), bounds.height());
            }
        );
        layout.addView(pauseBtn, btnParams);

        setContentView(layout);

    }

    private void checkAndSetHighscore(){
        if (gameView.score > highscore){
            highscore = gameView.score;
            editor.putInt("Highscore", highscore);
            editor.apply();
        }
    }

    public void showLostDialog() {
        checkAndSetHighscore();
        runOnUiThread(() -> {
            // Inflate the custom layout
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_game_over, null);

            // Create the dialog using the custom layout
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;

            // Set up the buttons
            Button retryButton = dialogView.findViewById(R.id.btnRetry);
            Button menuButton = dialogView.findViewById(R.id.btnQuit);

            retryButton.setOnClickListener(v -> {
                dialog.dismiss();
                gameView.resetGame();
            });

            menuButton.setOnClickListener(v -> {
                dialog.dismiss();
                startActivity(new Intent(GameActivity.this, MainActivity.class));
                finish();
            });

            TextView coinsView = dialogView.findViewById(R.id.gameOverCoins);
            DecimalFormat format = new DecimalFormat("#.0");
            String collectedStr = format.format(gameView.score * coinMultiplier);
            String[] split = collectedStr.split("\\.");
            if (Objects.equals(split[1], "0")) {
                collectedStr = split[0];
            }
            String text;
            if (gameView.score > 0)
                text = "You collected " + collectedStr + " coins";
            else
                text = "You collected " + 0 + " coins";
            coinsView.setText(text);

            // Show the dialog
            dialog.show();
        });
    }
    public void showWonDialog() {
        checkAndSetHighscore();
        runOnUiThread(() -> {
            // Inflate the custom layout
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_game_won, null);

            // Create the dialog using the custom layout
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;

            // Set up the button
            Button continueButton = dialogView.findViewById(R.id.btnContinue);

            continueButton.setOnClickListener(v -> {
                dialog.dismiss();
                startActivity(new Intent(GameActivity.this, MainActivity.class));
                finish();
            });

            TextView coinsView = dialogView.findViewById(R.id.gameOverCoins);
            DecimalFormat format = new DecimalFormat("#.0");
            String collectedStr = format.format(gameView.score * coinMultiplier);
            String[] split = collectedStr.split("\\.");
            if (Objects.equals(split[1], "0")) {
                collectedStr = split[0];
            }
            String text;
            if (gameView.score > 0)
                text = "You collected " + collectedStr + " coins";
            else
                text = "You collected " + 0 + " coins";
            coinsView.setText(text);
            // Show the dialog
            dialog.show();
        });
    }

    public void saveLevel() {
        editor.putInt("Level", gameView.levelNum);
        editor.apply();
    }

    public void addScoreToCoins(){
        double coins = sharedPref.getFloat("Coins", 0);
        editor.putFloat("Coins", (float) (coins + gameView.score * coinMultiplier));
        editor.apply();
    }


    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        saveLevel();
    }
}