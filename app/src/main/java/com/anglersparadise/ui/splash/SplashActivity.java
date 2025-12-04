// app/src/main/java/com/anglersparadise/ui/splash/SplashActivity.java
package com.anglersparadise.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.anglersparadise.databinding.ActivitySplashBinding;
import com.anglersparadise.ui.lake.LakeActivity;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.logo.setAlpha(0f);
        binding.logo.animate()
                .alpha(1f)
                .setDuration(500)
                .withEndAction(() -> binding.logo.animate()
                        .setStartDelay(400)
                        .alpha(0f)
                        .setDuration(500)
                        .withEndAction(() -> {
                            startActivity(new Intent(this, LakeActivity.class));
                            finish();
                        })
                        .start())
                .start();
    }
}
