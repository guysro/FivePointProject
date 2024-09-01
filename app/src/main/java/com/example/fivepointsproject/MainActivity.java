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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fivepointsproject.gameobjects.UpgradeFragment;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private TextView levelTextView;
    private TextView coinsTextView;

    private int shooterPowerLvl = 1;
    private int shooterSpeedLvl = 1;
    private int coinMultiplierLvl = 1;
    private SharedPreferences sharedPref;
    private  SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        findViewById(R.id.start).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, GameActivity.class)));

        sharedPref = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        levelTextView = findViewById(R.id.levelTextView);
        displayLevel();

        coinsTextView = findViewById(R.id.coinsTextView);
        Drawable coin = AppCompatResources.getDrawable(this, R.drawable.coin);
        assert coin != null;
        coin.setBounds(0, 0, 100, 100);
        coinsTextView.setCompoundDrawables(null, null, coin, null);
        displayCoins();

        shooterSpeedLvl = sharedPref.getInt("ShooterSpeedLevel", 1); // Default level is 1
        shooterPowerLvl = sharedPref.getInt("ShooterPowerLevel", 1); // Default level is 1
        coinMultiplierLvl = sharedPref.getInt("CoinMultiplierLevel", 1); // Default level is 1

        TabLayout tabLayout = findViewById(R.id.tabLayout);

        getSupportFragmentManager().beginTransaction().replace(R.id.upgradeFrame, new UpgradeFragment(sharedPref, ()-> {
                    shooterSpeedLvl++;
                    editor.putInt("ShooterSpeedLevel", shooterSpeedLvl);
                    editor.apply();
                }, () -> String.valueOf(shooterSpeedLvl), this::displayCoins))
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Fragment fragment = null;
                switch (tab.getPosition()){
                    case 0:
                        fragment = new UpgradeFragment(sharedPref, ()-> {
                            shooterSpeedLvl++;
                            editor.putInt("ShooterSpeedLevel", shooterSpeedLvl);
                            editor.apply();
                        }, () -> String.valueOf(shooterSpeedLvl), ()->displayCoins());
                        break;
                    case 1:
                        fragment = new UpgradeFragment(sharedPref, ()-> {
                            shooterPowerLvl++;
                            editor.putInt("ShooterPowerLevel", shooterPowerLvl);
                            editor.apply();
                        }, () -> String.valueOf(shooterPowerLvl), ()->displayCoins());
                        break;
                    case 2:
                        fragment = new UpgradeFragment(sharedPref, ()-> {
                            coinMultiplierLvl++;
                            editor.putInt("CoinMultiplierLevel", coinMultiplierLvl);
                            editor.apply();
                        }, () -> String.valueOf(coinMultiplierLvl), ()->displayCoins());
                        break;
                }
                assert fragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.upgradeFrame, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_NONE)
                        .commit();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        displayLevel();
        displayCoins();
    }

    private void displayLevel() {
        int level = sharedPref.getInt("Level", 1); // Default level is 1
        String text = String.valueOf(level);
        levelTextView.setText(text);
    }

    private void displayCoins() {
        double coins = sharedPref.getFloat("Coins", 0); // Default coin number is 0
        DecimalFormat format = new DecimalFormat("#.0");
        String text = format.format(coins);
        String[] split = text.split("\\.");
        if (Objects.equals(split[1], "0")) {
            text = split[0];
        }
        if (coins == 0)
            text = "0";
        coinsTextView.setText(text);
    }
}
