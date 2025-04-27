package io.diplom.models

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
abstract class LongEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    val id: Long? = null
}
