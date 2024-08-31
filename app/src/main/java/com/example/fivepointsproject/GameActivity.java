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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    private Rect bounds;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        bounds = getWindowManager().getCurrentWindowMetrics().getBounds();

        RelativeLayout layout = new RelativeLayout(this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        params.width = bounds.width();
        params.height = bounds.height();

        gameView = new GameView(this, bounds.width(), bounds.height());
        SharedPreferences sharedPref = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        gameView.levelNum = sharedPref.getInt("Level", 1); // Default level is 1

        layout.addView(gameView, params);

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

    public void showLostDialog() {
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

            // Show the dialog
            dialog.show();
        });
    }
    public void showWonDialog() {
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

            // Show the dialog
            dialog.show();
        });
    }

    public void saveLevel() {
        SharedPreferences sharedPref = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Level", gameView.levelNum);
        editor.apply();
    }

    public void addScoreToCoins(){
        SharedPreferences sharedPref = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        int coins = sharedPref.getInt("Coins", 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Coins", coins + gameView.score);
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        saveLevel();
    }
}