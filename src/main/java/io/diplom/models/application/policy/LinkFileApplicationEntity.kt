package io.diplom.models.application.policy

import io.diplom.models.LongEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "application_docs")
class LinkFileApplicationEntity(

    @Column(name = "user_id")
    val userId: Long? = null,

    @Column(name = "application_id")
    val applicationId: UUID? = null,

    @Column(name = "filename")
    val filename: String? = null

) : LongEntity()
