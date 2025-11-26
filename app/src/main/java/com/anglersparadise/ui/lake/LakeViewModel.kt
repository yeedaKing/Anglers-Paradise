// app/src/main/java/com/anglersparadise/ui/lake/LakeViewModel.kt

package com.anglersparadise.ui.lake

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

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
                _toast.tryEmit("Fish on!  Reel now!")
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
                _toast.tryEmit("Too early—fish got away.")
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
            val species = com.anglersparadise.domain.model.SpeciesCatalog.randomName()
            val size = com.anglersparadise.domain.model.SpeciesCatalog.randomSize()
            com.anglersparadise.data.FishRepository.addToTank(
                com.anglersparadise.data.FishRepository.newFish(species, size)
            )
            _toast.tryEmit("Added a $species (size $size) to tank.")
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
