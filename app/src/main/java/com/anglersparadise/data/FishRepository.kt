// app/src/main/java/com/anglersparadise/data/FishRepository.kt

package com.anglersparadise.data

import com.anglersparadise.domain.model.Fish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong

object FishRepository {
    private val nextId = AtomicLong(1L)

    private val _tank = MutableStateFlow<List<Fish>>(emptyList())
    val tank: StateFlow<List<Fish>> = _tank

    private val _history = MutableStateFlow<List<Fish>>(emptyList())
    val history: StateFlow<List<Fish>> = _history

    fun newFish(species: String = "Common Fish", size: Int = 1): Fish =
        Fish(id = nextId.getAndIncrement(), species = species, size = size)

    fun addToTank(fish: Fish) {
        _tank.update { it + fish }
        // Record ever-caught for basket:
        _history.update { it + fish }
    }

    fun removeFromTank(id: Long) {
        _tank.update { it.filterNot { f -> f.id == id } }
        // history remains (ever-caught)
    }

    fun clearTank() {
        _tank.value = emptyList()
    }
}
