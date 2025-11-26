// app/src/main/java/com/anglersparadise/domain/model/Fish.kt

package com.anglersparadise.domain.model

data class Fish(
    val id: Long,
    val species: String = "Common Fish",
    val size: Int = 1,
    val caughtAt: Long = System.currentTimeMillis()
)
