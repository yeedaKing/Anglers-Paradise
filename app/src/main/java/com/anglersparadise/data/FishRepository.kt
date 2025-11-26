// app/src/main/java/com/anglersparadise/data/FishRepository.kt
package com.anglersparadise.data

import com.anglersparadise.data.local.TankStore
import com.anglersparadise.domain.model.Fish
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicLong

object FishRepository {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val saveMutex = Mutex()

    private val nextId = AtomicLong(1L)

    private val _tank = MutableStateFlow<List<Fish>>(emptyList())
    val tank: StateFlow<List<Fish>> = _tank

    private val _history = MutableStateFlow<List<Fish>>(emptyList())
    val history: StateFlow<List<Fish>> = _history

    init {
        // Load persisted data once (no saving during load)
        scope.launch {
            TankStore.tankFlow.collectLatest { list ->
                _tank.value = list
                list.maxByOrNull { it.id }?.let { if (it.id + 1 > nextId.get()) nextId.set(it.id + 1) }
            }
        }
        scope.launch {
            TankStore.historyFlow.collectLatest { list ->
                _history.value = list
                list.maxByOrNull { it.id }?.let { if (it.id + 1 > nextId.get()) nextId.set(it.id + 1) }
            }
        }
    }

    fun newFish(species: String, size: Int): Fish =
        Fish(id = nextId.getAndIncrement(), species = species, size = size)

    fun addToTank(fish: Fish) {
        // Atomically apply both list updates
        _tank.update { it + fish }
        _history.update { it + fish }

        // Persist the resulting state once, in order
        scope.launch {
            saveMutex.withLock {
                TankStore.saveTank(_tank.value)
                TankStore.saveHistory(_history.value)
            }
        }
    }

    fun removeFromTank(id: Long) {
        _tank.update { list -> list.filterNot { it.id == id } }
        scope.launch {
            saveMutex.withLock {
                TankStore.saveTank(_tank.value)
            }
        }
        // history is ever-caught → do not remove
    }

    fun clearTank() {
        _tank.value = emptyList()
        scope.launch {
            saveMutex.withLock {
                TankStore.saveTank(_tank.value)
            }
        }
    }
}
