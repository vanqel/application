package io.diplom.security.models

data class PersonEntity(

    val id: Int,

    var extId: Int,

    val snils: String,

    val passportSerial: String,

    val passportNumber: String,

    val firstName: String,

    val lastName: String,

    val secondName: String?
)
