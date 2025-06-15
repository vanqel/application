package io.diplom.models.application.policy

import io.diplom.models.application.additional.ApplicationAdditionalPersons
import io.diplom.models.dictionary.Car
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "casco_application")
class CascoApplicationEntity(
    @Column(name = "kbm")
    val kbm: Double = 1.0,

    @ManyToOne
    @JoinColumn(name = "car")
    val modelAuto: Car? = null,

    @Column(name = "autonum")
    val numAuto: String? = null,

    @Column(name = "ctc")
    var stsNum: String? = null,

    @Column(name = "tech_view")
    var techView: String? = null,

) : AbstractApplicationEntity() {

    override fun getType(): ApplicationDetails.Type = ApplicationDetails.Type.CASCO
}

