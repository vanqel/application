package io.diplom.models.application.additional

import io.diplom.models.LongEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "application_person_additional")
class ApplicationAdditionalPersons(

    @Column(name = "full_name")
    val fullName: String? = null,

    @Column(name = "age")
    val age: Long? = null,

    @Column(name = "stage")
    val stage: Long? = null,

    @Column(name = "date_issue")
    val dateIssue: LocalDate? = null,

    @Column(name = "serial")
    val serial: String? = null,

    @Column(name = "number")
    val number: String? = null,

    @Column(name = "application_id")
    val applicationId: UUID? = null

): LongEntity()
