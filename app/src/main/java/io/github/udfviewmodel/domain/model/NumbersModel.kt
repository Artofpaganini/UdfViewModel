package io.github.udfviewmodel.domain.model

import java.util.UUID

data class NumbersModel(
    val id: String,
    val data: List<String>
) {
    companion object {
        fun empty(): NumbersModel = NumbersModel(id = UUID.randomUUID().toString(), emptyList())
    }
}

