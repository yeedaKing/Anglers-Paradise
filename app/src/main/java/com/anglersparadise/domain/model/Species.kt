// app/src/main/java/com/anglersparadise/domain/model/Species.kt

package com.anglersparadise.domain.model

object SpeciesCatalog {
    private val names = listOf(
        "Bluegill", "Bass", "Carp", "Trout", "Catfish",
        "Perch", "Pike", "Salmon", "Walleye", "Sunfish"
    )

    fun randomName(): String = names.random()

    /** Size bucket 1..5 (affects tank sprite size) */
    fun randomSize(): Int = (1..5).random()
}
