package com.mfrdev.osm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mfrdev.osm.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.mapButton.setOnClickListener(this::clickEvent);
    }

    private void clickEvent(View view) {
        startActivity(new Intent(MainActivity.this, OSMActivity.class));
    }
}