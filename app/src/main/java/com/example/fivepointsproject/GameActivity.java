package com.example.fivepointsproject;

import android.app.AlertDialog;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
            new AlertDialog.Builder(this)
                    .setTitle("Game Over")
                    .setMessage("You lost! Try again?")
                    .setPositiveButton("Retry", (dialog, which) -> {
                        // Restart the game
                        gameView.resetGame();
                    })
                    .setNegativeButton("Quit", (dialog, which) -> {
                        // Exit the game
                        finish();
                    })
                    .setCancelable(false)
                    .show();
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