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

        // Start fully transparent
        binding.logo.alpha = 0f

        // Fade in (500ms), hold (400ms), fade out (500ms), then go to Lake
        binding.logo.animate()
            .alpha(1f)
            .setDuration(500)
            .withEndAction {
                binding.logo.animate()
                    .setStartDelay(400)
                    .alpha(0f)
                    .setDuration(500)
                    .withEndAction {
                        startActivity(Intent(this, LakeActivity::class.java))
                        finish()
                    }
                    .start()
            }
            .start()
    }
}
