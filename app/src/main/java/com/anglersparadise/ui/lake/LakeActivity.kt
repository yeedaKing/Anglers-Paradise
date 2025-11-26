// app/src/main/java/com/anglersparadise/ui/lake/LakeActivity.kt

package com.anglersparadise.ui.lake

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.anglersparadise.databinding.ActivityLakeBinding
import com.anglersparadise.ui.tank.FishTankActivity
import kotlinx.coroutines.launch

class LakeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLakeBinding
    private val vm: LakeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Buttons
        binding.btnCast.setOnClickListener { vm.cast() }
        binding.btnReel.setOnClickListener { vm.reel() }
        binding.btnPutInTank.setOnClickListener {
            if (vm.state.value == LakeState.CAUGHT) {
                vm.confirmCatchToTank()
                Toast.makeText(this, "Added to tank (stub)", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnLetGo.setOnClickListener {
            if (vm.state.value == LakeState.CAUGHT) {
                vm.letGo()
                Toast.makeText(this, "Released", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnGoToTank.setOnClickListener {
            startActivity(Intent(this, FishTankActivity::class.java))
        }

        // Observe state and update UI
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.state.collect { s ->
                        binding.status.text = when (s) {
                            LakeState.IDLE -> "Ready to cast."
                            LakeState.WAITING -> "Waiting…"
                            LakeState.HOOKED -> "Fish on!  Reel now!"
                            LakeState.CAUGHT -> "You caught a fish."
                            LakeState.ESCAPED -> "The fish got away."
                        }
                        updateEnabledButtons(s)
                        binding.lakeCanvas.setLakeState(s)
                        if (s == LakeState.HOOKED) vibrateOnHooked()
                    }
                }
                launch {
                    vm.toast.collect { msg ->
                        Toast.makeText(this@LakeActivity, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateEnabledButtons(s: LakeState) {
        binding.btnCast.isEnabled = s == LakeState.IDLE
        binding.btnReel.isEnabled = (s == LakeState.WAITING || s == LakeState.HOOKED)
        binding.btnPutInTank.isEnabled = s == LakeState.CAUGHT
        binding.btnLetGo.isEnabled = s == LakeState.CAUGHT
    }

    private fun vibrateOnHooked() {
        val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(80)
        }
    }
}
