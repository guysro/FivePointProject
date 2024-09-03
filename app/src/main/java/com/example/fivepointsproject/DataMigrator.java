package com.example.fivepointsproject;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DataMigrator {

    private static final String SHARED_PREFS_NAME = "GamePrefs";
    private static final String KEY_HIGHSCORE = "Highscore";
    private static final String KEY_UPGRADE_SPEED = "ShooterSpeedLvl";
    private static final String KEY_UPGRADE_POWER = "ShooterPowerLvl";
    private static final String KEY_COIN_MULTIPLIER = "CoinMultiplierLvl";
    private static final String KEY_COIN_AMOUNT = "Coins";
    private static final String KEY_LEVEL = "coinMultiplier";

    private Context context;

    public DataMigrator(Context context) {
        this.context = context;
    }

    public void migrateData() {
        // Get the currently logged-in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // No user is logged in
            return;
        }
        String uid = user.getUid();

        // Retrieve data from SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        int highscore = prefs.getInt(KEY_HIGHSCORE, 0);
        int upgradeSpeed = prefs.getInt(KEY_UPGRADE_SPEED, 1);
        int upgradePower = prefs.getInt(KEY_UPGRADE_POWER, 1);
        int coinMultiplier = prefs.getInt(KEY_COIN_MULTIPLIER, 1);
        int level = prefs.getInt(KEY_LEVEL, 1);
        double coinAmount = prefs.getFloat(KEY_COIN_AMOUNT, 0);

        // Prepare the data to be saved in Firebase
        UserData userData = new UserData(highscore, upgradeSpeed, upgradePower, coinMultiplier, level, coinAmount);

        // Save the data under the user's UID in Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid);
        databaseReference.setValue(userData);
    }

    // Define a UserData class to hold the data
    public static class UserData {
        public int highscore;
        public int shooterSpeedLvl;
        public int shooterPowerLvl;
        public int coinMultiplierLvl;
        public int level;
        public double coinAmount;

        public UserData() {
            // Default constructor required for Firebase
        }

        public UserData(int highscore, int shooterSpeedLvl, int shooterPowerLvl, int coinMultiplierLvl, int level, double coinAmount) {
            this.highscore = highscore;
            this.shooterSpeedLvl = shooterSpeedLvl;
            this.shooterPowerLvl = shooterPowerLvl;
            this.coinMultiplierLvl = coinMultiplierLvl;
            this.coinAmount = coinAmount;
            this.level = level;
        }
    }
    public static void updateHighScore(int newHighScore) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("highscore");
            databaseReference.setValue(newHighScore);
        }
    }

    public static void updateShooterPower(int newShooterPower) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("shooterPowerLvl");
            databaseReference.setValue(newShooterPower);
        }
    }

    public static void updateShooterSpeed(int newShooterSpeed) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("shooterSpeedLvl");
            databaseReference.setValue(newShooterSpeed);
        }
    }

    public static void updateCoinMultiplier(int newMultiplier) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("coinMultiplierLvl");
            databaseReference.setValue(newMultiplier);
        }
    }

    public static void updateLevel(int newLevel) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("level");
            databaseReference.setValue(newLevel);
        }
    }

    public static void updateCoinAmount(double newCoinAmount) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid).child("coinAmount");
            databaseReference.setValue(newCoinAmount);
        }
    }

    public static void loadUserData(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserData userData = dataSnapshot.getValue(UserData.class);
                    if (userData != null) {
                        editor.putInt(KEY_HIGHSCORE, userData.highscore);
                        editor.putInt(KEY_UPGRADE_SPEED, userData.shooterSpeedLvl);
                        editor.putInt(KEY_UPGRADE_POWER, userData.shooterPowerLvl);
                        editor.putInt(KEY_COIN_MULTIPLIER, userData.coinMultiplierLvl);
                        editor.putFloat(KEY_COIN_AMOUNT, (float) userData.coinAmount);
                        editor.putInt(KEY_LEVEL, userData.level);
                        editor.apply();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        }
    }

}
