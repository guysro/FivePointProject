package com.example.fivepointsproject;

import android.app.AlertDialog;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class GameActivity extends AppCompatActivity {
    private GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Rect bound = getWindowManager().getCurrentWindowMetrics().getBounds();
        gameView = new GameView(this, bound.width(), bound.height());

        setContentView(gameView);
    }
    public void showGameOverDialog() {
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
            Button quitButton = dialogView.findViewById(R.id.btnQuit);

            retryButton.setOnClickListener(v -> {
                dialog.dismiss();
                gameView.resetGame();
            });

            quitButton.setOnClickListener(v -> {
                dialog.dismiss();
                finish();
            });

            // Show the dialog
            dialog.show();
        });
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
}