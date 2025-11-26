// app/src/main/java/com/anglersparadise/ui/tank/TankViewModel.kt

package com.anglersparadise.ui.tank

import androidx.lifecycle.ViewModel
import com.anglersparadise.data.FishRepository
import kotlinx.coroutines.flow.StateFlow

class TankViewModel : ViewModel() {
    val tankFish: StateFlow<List<com.anglersparadise.domain.model.Fish>> = FishRepository.tank

    fun releaseFish(id: Long) = FishRepository.removeFromTank(id)
    fun releaseAll() = FishRepository.clearTank()
}
