package io.github.udfviewmodel.domain.model

import java.util.UUID

data class CitiesModel(
    val id: String,
    val data: List<String>
) {
    companion object {
        fun empty(): CitiesModel = CitiesModel(id = UUID.randomUUID().toString(),emptyList())
    }
}

