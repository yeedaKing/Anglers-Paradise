// app/src/main/java/com/anglersparadise/ui/tank/FishTankActivity.java
package com.anglersparadise.ui.tank;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.anglersparadise.databinding.ActivityFishTankBinding;

public class FishTankActivity extends AppCompatActivity {

    private ActivityFishTankBinding binding;
    private TankViewModel vm;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFishTankBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(TankViewModel.class);

        binding.btnOpenBasket.setOnClickListener(v ->
                new FishBasketDialog().show(getSupportFragmentManager(), "basket")
        );
        binding.btnBackToLake.setOnClickListener(v -> finish());
        binding.btnReleaseAll.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Empty tank?")
                        .setMessage("Are you sure you want to release all fish?")
                        .setPositiveButton("Yes", (d, w) -> vm.releaseAll())
                        .setNegativeButton("Cancel", null)
                        .show()
        );

        vm.tankFish.observe(this, list -> {
            if (list == null) return;
            binding.tankCanvas.setFish(list);
            binding.count.setText("Fish in tank: " + list.size());
        });
    }
}
