package io.github.udfviewmodel.domain.model

import java.util.UUID

data class CarsModel(
    val id: String,
    val data: List<String>
) {
    companion object {
        fun empty(): CarsModel = CarsModel(id = UUID.randomUUID().toString(),emptyList())
    }
}

