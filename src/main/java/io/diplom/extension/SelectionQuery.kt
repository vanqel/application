package io.diplom.extension

import io.diplom.config.PaginationInput
import org.hibernate.reactive.mutiny.Mutiny

fun <T> Mutiny.SelectionQuery<T>.pagination(paginationInput: PaginationInput): Mutiny.SelectionQuery<T?> =
    this.setFirstResult(paginationInput.page)
        .setMaxResults(paginationInput.size + 1)
