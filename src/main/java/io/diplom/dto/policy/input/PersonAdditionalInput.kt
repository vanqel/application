package io.diplom.dto.policy.input

import io.diplom.models.application.additional.ApplicationAdditionalPersons
import io.diplom.models.application.policy.AbstractApplicationEntity
import java.time.LocalDate

/**
 * ФИО,
 * возраст водителя,
 * стаж,
 * дата выдачи документа,
 * серия,
 * номер
 */
data class PersonAdditionalInput(
    val fullName: String,
    val age: Long,
    val stage: Long,
    val dateIssue: LocalDate,
    val serial: String,
    val number: String
) {
    fun toEntity(applicationEntity: AbstractApplicationEntity) = ApplicationAdditionalPersons(
        fullName = fullName,
        age = age,
        stage = stage,
        dateIssue = dateIssue,
        serial = serial,
        number = number,
        applicationId = applicationEntity.id!!
    )
}
