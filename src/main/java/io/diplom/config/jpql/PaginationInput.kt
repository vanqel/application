package io.diplom.config.jpql

data class PaginationInput(
    val page: Int,
    val size: Int
){
    companion object {
        fun single() = PaginationInput(0,1)
    }
}
