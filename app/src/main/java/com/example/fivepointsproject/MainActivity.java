package com.example.fivepointsproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.fivepointsproject.gameobjects.UpgradeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private TextView levelTextView;
    private TextView highscoreTextView;
    private TextView coinsTextView;

    private int shooterPowerLvl = 1;
    private int shooterSpeedLvl = 1;
    private int coinMultiplierLvl = 1;
    private SharedPreferences sharedPref;
    private  SharedPreferences.Editor editor;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        findViewById(R.id.start).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, GameActivity.class)));

        sharedPref = getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        mAuth = FirebaseAuth.getInstance();
        DataMigrator.loadUserData(this);

        levelTextView = findViewById(R.id.levelTextView);
        displayLevel();
        highscoreTextView = findViewById(R.id.highScoreTextView);
        displayHighScore();

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
            loadData();
            shooterSpeedLvl++;
            editor.putInt("ShooterSpeedLevel", shooterSpeedLvl);
            editor.apply();
//            DataMigrator.migrateData(this);
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
                            loadData();
                            shooterSpeedLvl++;
                            editor.putInt("ShooterSpeedLevel", shooterSpeedLvl);
                            editor.apply();
//                            migrateData();
                        }, () -> String.valueOf(shooterSpeedLvl), ()->displayCoins());
                        break;
                    case 1:
                        fragment = new UpgradeFragment(sharedPref, ()-> {
                            loadData();
                            shooterPowerLvl++;
                            editor.putInt("ShooterPowerLevel", shooterPowerLvl);
                            editor.apply();
//                            migrateData();
                        }, () -> String.valueOf(shooterPowerLvl), ()->displayCoins());
                        break;
                    case 2:
                        fragment = new UpgradeFragment(sharedPref, ()-> {
                            loadData();
                            coinMultiplierLvl++;
                            editor.putInt("CoinMultiplierLevel", coinMultiplierLvl);
                            editor.apply();
//                            migrateData();
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

        ImageView settingsBtn = findViewById(R.id.imageView);
        settingsBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {

                if (menuItem.getItemId() == R.id.nav_register){
                    displayRegisterDialog();
                    return true;
                }
                else if (menuItem.getItemId() == R.id.nav_logout){
                    logOut();
                    return true;
                }
                else if (menuItem.getItemId() == R.id.nav_login){
                    displayLoginDialog();
                    return true;
                }

                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        displayLevel();
        displayCoins();
    }

    private void displayLevel() {
        loadData();
        int level = sharedPref.getInt("Level", 1); // Default level is 1
        String text = "Level " + level;
        levelTextView.setText(text);
    }

    private void displayHighScore() {
        loadData();
        int hs = sharedPref.getInt("Highscore", 0); // Default level is 1
        String text = "Highscore: " + hs;
        highscoreTextView.setText(text);
    }

    private void displayCoins() {
        loadData();
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
        migrateData();
    }

    private void logOut(){
        mAuth.signOut();
    }
    private void displayLoginDialog(){
        runOnUiThread(() -> {
            // Inflate the custom layout
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_login, null);

            // Create the dialog using the custom layout
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;

            ImageView exit = dialogView.findViewById(R.id.exit);
            exit.setOnClickListener(v->{
                dialog.dismiss();
            });

            // Set up the button
            Button confirmBtn = dialogView.findViewById(R.id.btnLogin);

            TextView nameError = dialogView.findViewById(R.id.emailError);
            TextView passwordError = dialogView.findViewById(R.id.passwordError);

            EditText nameInput = dialogView.findViewById(R.id.email);
            EditText passwordInput = dialogView.findViewById(R.id.password);

            confirmBtn.setOnClickListener(v -> {
                String email = nameInput.getText().toString();
                String password = passwordInput.getText().toString();
                boolean isOk = true;
                System.out.println("email: " + email);
                System.out.println("password: " + password);

                nameError.setText("");
                passwordError.setText("");

                if (isInvalidEmail(email)){
                    nameError.setText(R.string.not_a_valid_email);
                    isOk = false;
                }
                if (password.length() < 8){
                    passwordError.setText(R.string.pass_not_long);
                    isOk = false;
                }

                if (isOk){
                    mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                dialog.dismiss();
                                loadData();
                                displayCoins();
                                displayLevel();
                                displayHighScore();
                            }
                            else passwordError.setText(R.string.password_email_wrong);
                        });
                }
            });
            // Show the dialog
            dialog.show();
        });
    }


    private void displayRegisterDialog(){
        runOnUiThread(() -> {
            // Inflate the custom layout
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_register, null);

            // Create the dialog using the custom layout
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);
            builder.setCancelable(false);

            AlertDialog dialog = builder.create();
            Objects.requireNonNull(dialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;

            ImageView exit = dialogView.findViewById(R.id.exit);
            exit.setOnClickListener(v->{
                dialog.dismiss();
            });

            // Set up the button
            Button confirmBtn = dialogView.findViewById(R.id.btnRegister);

            TextView passwordError = dialogView.findViewById(R.id.passwordErrorR);
            TextView cPasswordError = dialogView.findViewById(R.id.confirmPasswordErrorR);
            TextView emailError = dialogView.findViewById(R.id.emailErrorR);

            EditText passwordInput = dialogView.findViewById(R.id.passwordRegister);
            EditText cPasswordInput = dialogView.findViewById(R.id.passwordConfirmRegister);
            EditText emailInput = dialogView.findViewById(R.id.emailRegister);

            confirmBtn.setOnClickListener(v -> {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String passwordCon = cPasswordInput.getText().toString();

                boolean isOk = true;
                System.out.println("email: " + email);
                System.out.println("password: " + password);
                System.out.println("password confirm: " + passwordCon);

                emailError.setText("");
                passwordError.setText("");
                cPasswordError.setText("");

                if (password.length() < 8){
                    passwordError.setText(R.string.pass_not_long);
                    isOk = false;
                }
                if (!password.equals(passwordCon)){
                    cPasswordError.setText(R.string.passwords_dont_match);
                    isOk = false;
                }
                if (isInvalidEmail(email)){
                    emailError.setText(R.string.not_a_valid_email);
                    isOk = false;
                }


                if (isOk){
                    mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                dialog.dismiss();
                                resetData();
                                displayCoins();
                                displayLevel();
                                displayHighScore();
                            }
                            else {
                                if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                    cPasswordError.setText(R.string.user_already_exists);
                                else cPasswordError.setText(R.string.failed_to_register_user);
                            }
                        });
                }
            });
            // Show the dialog
            dialog.show();
        });
    }

    private void loadData(){
        DataMigrator.loadUserData(this);
    }
    private void migrateData(){
        DataMigrator.migrateData(this);
    }
    private void resetData(){
        DataMigrator.resetData(this);
    }

    public static boolean isInvalidEmail(String email) {
        // Regular expression for a valid email address
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        // Compile the regex
        Pattern pattern = Pattern.compile(emailRegex);

        // If the email is null, return false
        if (email == null) {
            return true;
        }

        // Match the email with the regex
        Matcher matcher = pattern.matcher(email);

        // Return true if the email matches the regex, false otherwise
        return !matcher.matches();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DataMigrator.migrateData(this);
    }
}
