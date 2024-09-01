package com.example.fivepointsproject.gameobjects;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fivepointsproject.R;

import java.text.DecimalFormat;
import java.util.function.Supplier;

public class UpgradeFragment extends Fragment {

    TextView upgrade;
    TextView title;
    TextView level;

    Runnable upgradeFunc;
    Runnable displayFunc;
    SharedPreferences sharedPref;
    Supplier<String> value;

    double price;
    public UpgradeFragment(SharedPreferences sharedPref, Runnable upgradeFunc, Supplier<String> value, Runnable displayFunc){
        this.sharedPref = sharedPref;
        this.upgradeFunc = upgradeFunc;
        this.value = value;
        this.displayFunc = displayFunc;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upgrade, container, false);

        upgrade = view.findViewById(R.id.upgrade);
        Drawable coin = ResourcesCompat.getDrawable(view.getResources(), R.drawable.coin, null);
        assert coin != null;
        coin.setBounds(0, 0, 100, 100);
        upgrade.setCompoundDrawables(null, null, coin, null);

        level = view.findViewById(R.id.level);
        level.setText(value.get());
        price = Math.pow(Double.parseDouble(value.get()), 1.4);
        DecimalFormat format = new DecimalFormat("#.0");
        String t = format.format(price);
        upgrade.setText(t);

        float c = sharedPref.getFloat("Coins", 0);
        upgrade.setEnabled(c >= price);

        upgrade.setOnClickListener(v -> {
            upgradeFunc.run();
            System.out.println(price);
            String text = format.format(price);
            float coins = sharedPref.getFloat("Coins", 0);
            coins -= (float) price;
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putFloat("Coins", coins);
            editor.apply();
            System.out.println(coins);
            upgrade.setEnabled(coins >= price);
            displayFunc.run();
            String newVal = value.get();
            level.setText(newVal);
            price = Math.pow(Double.parseDouble(newVal), 1.4);
            ((TextView) v).setText(text);
        });
        title = view.findViewById(R.id.title);
        return view;
    }
}