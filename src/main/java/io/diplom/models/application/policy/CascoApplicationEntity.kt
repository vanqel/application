package io.diplom.models.application.policy

import io.diplom.models.dictionary.Car
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "casco_application")
class CascoApplicationEntity(

    val kbm: Double,

    @ManyToOne
    val modelAuto: Car,

    val numAuto: String
) : AbstractApplicationEntity() {

    override fun getType(): ApplicationDetails.Type = ApplicationDetails.Type.CASCO
}

