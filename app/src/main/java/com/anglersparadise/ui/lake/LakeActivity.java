// app/src/main/java/com/anglersparadise/ui/lake/LakeActivity.java
package com.anglersparadise.ui.lake;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.anglersparadise.databinding.ActivityLakeBinding;
import com.anglersparadise.ui.tank.FishTankActivity;

public class LakeActivity extends AppCompatActivity {

    private ActivityLakeBinding binding;
    private LakeViewModel vm;
    private LakeState lastState = null;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLakeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(LakeViewModel.class);

        binding.btnCast.setOnClickListener(v -> vm.cast());
        binding.btnReel.setOnClickListener(v -> vm.reel());
        binding.btnPutInTank.setOnClickListener(v -> vm.confirmCatchToTank());
        binding.btnLetGo.setOnClickListener(v -> {
            vm.letGo();
            Toast.makeText(this, "Released", Toast.LENGTH_SHORT).show();
        });
        binding.btnGoToTank.setOnClickListener(v ->
                startActivity(new Intent(this, FishTankActivity.class))
        );

        vm.state.observe(this, s -> {
            if (s == null) return;
            switch (s) {
                case IDLE:    binding.status.setText("Ready to cast."); break;
                case WAITING: binding.status.setText("Waiting…"); break;
                case HOOKED:  binding.status.setText("Fish on!  Reel now!"); break;
                case CAUGHT:  binding.status.setText("You caught a fish."); break;
                case ESCAPED: binding.status.setText("The fish got away."); break;
            }
            updateButtons(s);
            binding.lakeCanvas.setLakeState(s);

            if (lastState != LakeState.HOOKED && s == LakeState.HOOKED) vibrateOnHook();
            lastState = s;
        });

        vm.toast.observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateButtons(LakeState s) {
        binding.btnCast.setEnabled(s == LakeState.IDLE);
        binding.btnReel.setEnabled(s == LakeState.WAITING || s == LakeState.HOOKED);
        boolean caught = (s == LakeState.CAUGHT);
        binding.btnPutInTank.setEnabled(caught);
        binding.btnLetGo.setEnabled(caught);
    }

    private void vibrateOnHook() {
        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vib == null) return;
        if (Build.VERSION.SDK_INT >= 26) {
            vib.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //noinspection deprecation
            vib.vibrate(40);
        }
    }
}
