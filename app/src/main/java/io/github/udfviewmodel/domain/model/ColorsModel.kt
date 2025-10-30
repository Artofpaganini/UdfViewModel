package io.github.udfviewmodel.domain.model

import java.util.UUID

data class ColorsModel(
    val id: String,
    val data: List<String>
) {
    companion object {
        fun empty(): ColorsModel = ColorsModel(id = UUID.randomUUID().toString(),emptyList())
    }
}

