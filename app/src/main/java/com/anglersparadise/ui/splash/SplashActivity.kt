// app/src/main/java/com/angersparadise/ui/splash/SpashActivity.kt

package com.anglersparadise.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anglersparadise.databinding.ActivitySplashBinding
import com.anglersparadise.ui.lake.LakeActivity

/**
 * Shows the logo, fades in/out, then navigates to the Lake screen.
 */

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Start transparent, fade in -> brief hold -> fade out -> go to Lake
        binding.logo.alpha = 0f
        binding.logo.animate()
            .alpha(1f)
            .setDuration(700)
            .withEndAction {
                binding.logo.animate()
                    .alpha(0f)
                    .setStartDelay(300)
                    .setDuration(700)
                    .withEndAction {
                        startActivity(Intent(this, LakeActivity::class.java))
                        finish()
                    }
                    .start()
            }
            .start()
    }
}