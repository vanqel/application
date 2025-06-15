package io.diplom.security.models

import io.diplom.models.PersonDocuments
import java.time.LocalDate

data class PersonEntity(

    val id: Int,

    val name: String,

    val surname: String,

    val secondName: String,

    val birthDate: LocalDate,

    val documents: List<PersonDocuments>
)
