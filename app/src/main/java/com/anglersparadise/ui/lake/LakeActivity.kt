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
//import com.anglersparadise.databinding.ActivityLakePlaceholderBinding

/**
 * Placeholder screen. Replace this with real Lake UI/logic later
 */

enum class LakeState { IDLE, WAITING, HOOKED, CAUGHT, ESCAPED }

class LakeViewModel : ViewModel() {
    private val _state = MutableStateFlow(LakeState.IDLE)
    val state = _state.asStateFlow()

    private val _toast = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val toast = _toast.asSharedFlow()

    private var waitJob: Job? = null

    fun cast() {
        if (_state.value != LakeState.IDLE) return
        _state.value = LakeState.WAITING

        waitJob?.cancel()
        waitJob = viewModelScope.launch {
            val delayMs = Random.nextLong(1500L, 4000L)
            delay(delayMs)
            if (_state.value == LakeState.WAITING) {
                _state.value = LakeState.HOOKED
                _toast.tryEmit("Fish on! Reel now!")
            }
        }
    }
    fun reel() {
        when (_state.value) {
            LakeState.HOOKED -> {
                _state.value = LakeState.CAUGHT
                _toast.tryEmit("You caught a fish.")
            }
            LakeState.WAITING -> {
                _state.value = LakeState.ESCAPED
                _toast.tryEmit("Too early-fish got away.")
                viewModelScope.launch {
                    delay(1200)
                    resetToIdle()
                }
            }
            else -> Unit
        }
    }
    fun confirmCatchToTank() {
        if (_state.value == LakeState.CAUGHT) {
            // TODO: Add to repository when data layer is introduced
            resetToIdle()
        }
    }
    fun letGo() {
        if (_state.value == LakeState.CAUGHT) {
            resetToIdle()
        }
    }
    private fun resetToIdle() {
        waitJob?.cancel()
        _state.value = LakeState.IDLE
    }
}
class LakeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLakePlaceholderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLakePlaceholderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Nothing else yet
    }
}






