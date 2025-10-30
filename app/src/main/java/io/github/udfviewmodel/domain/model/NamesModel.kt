package io.github.udfviewmodel.domain.model

import java.util.UUID

data class NamesModel(
    val id: String,
    val data: List<String>
) {
    companion object {
        fun empty(): NamesModel = NamesModel(id = UUID.randomUUID().toString(),emptyList())
    }
}

