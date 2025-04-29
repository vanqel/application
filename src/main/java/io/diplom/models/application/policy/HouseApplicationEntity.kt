package io.diplom.models.application.policy

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Transient

@Entity
@Table(name = "house_application")
class HouseApplicationEntity(

    /** Стоимость отделки */
    @Column(name = "finishing_cost")
    var finishingCost: Double? = null,

    /** Стоимость конструктивные элементы */
    @Column(name = "structural_el_cost")
    var structuralElCost: Double? = null,

    /** Стоимость гражданской ответственности */
    @Column(name = "neighbors_cost")
    var neighborsCost: Double? = null,

    /** Стоимость домашнего имущества */
    @Column(name = "household")
    var household: Double? = null,

    /** Оплата в месяц */
    @Column(name = "month_cost")
    var monthCost: Double? = null,

    /**
     * ФИАС Адресс
     */
    @Column(name = "fias_address")
    var fiasAddress: String? = null,

    ) : AbstractApplicationEntity() {

    @Transient
    var address: String? = null

    override fun getType(): ApplicationDetails.Type = ApplicationDetails.Type.HOUSE

}

