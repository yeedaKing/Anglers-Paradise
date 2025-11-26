// app/src/main/java/com/anglersparadise/ui/tank/FishTankActivity.kt

package com.anglersparadise.ui.tank

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.anglersparadise.databinding.ActivityFishTankBinding
import kotlinx.coroutines.launch

class FishTankActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFishTankBinding
    private val vm: TankViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFishTankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOpenBasket.setOnClickListener {
            FishBasketDialog().show(supportFragmentManager, "basket")
        }

        binding.btnBackToLake.setOnClickListener { finish() }

        binding.btnReleaseAll.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Empty tank?")
                .setMessage("Are you sure you want to release all fish?")
                .setPositiveButton("Yes") { _, _ -> vm.releaseAll() }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // Observe tank fish -> feed the canvas + list text
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.tankFish.collect { list ->
                    binding.tankCanvas.setFish(list)
                    binding.count.text = "Fish in tank: ${list.size}"
                }
            }
        }
    }
}
