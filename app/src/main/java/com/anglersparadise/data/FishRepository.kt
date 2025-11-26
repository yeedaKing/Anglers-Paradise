// app/src/main/java/com/anglersparadise/data/FishRepository.kt

package com.anglersparadise.data

import com.anglersparadise.domain.model.Fish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong

/**
 * MVP in-memory store.  Swap to Room/DataStore later.
 */
object FishRepository {
    private val nextId = AtomicLong(1L)
    private val _tank = MutableStateFlow<List<Fish>>(emptyList())
    val tank: StateFlow<List<Fish>> = _tank

    fun newFish(species: String = "Common Fish", size: Int = 1): Fish =
        Fish(id = nextId.getAndIncrement(), species = species, size = size)

    fun addToTank(fish: Fish) {
        _tank.update { it + fish }
    }

    fun removeFromTank(id: Long) {
        _tank.update { it.filterNot { f -> f.id == id } }
    }

    fun clearTank() {
        _tank.value = emptyList()
    }
}
