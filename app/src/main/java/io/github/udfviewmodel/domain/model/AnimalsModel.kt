package io.github.udfviewmodel.domain.model

import java.util.UUID

data class AnimalsModel(
    val id: String,
    val data: List<String>
) {
    companion object {
        fun empty(): AnimalsModel = AnimalsModel(id = UUID.randomUUID().toString(), emptyList())
    }
}

