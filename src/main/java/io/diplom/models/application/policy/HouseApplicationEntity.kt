package io.diplom.models.application.policy

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Transient

@Entity
@Table(name = "house_application")
class HouseApplicationEntity(

    /** Стоимость отделки */
    val finishingCost: Double? = null,

    /** Стоимость конструктивные элементы */
    val structuralElCost: Double? = null,

    /** Стоимость гражданской ответственности */
    val neighborsCost: Double? = null,

    /** Стоимость домашнего имущества */
    val household: Double? = null,

    /** Оплата в месяц */
    val monthCost: Double? = null,

    /**
     * ФИАС Адресс
     */
    val fiasAddress: String,

    ) : AbstractApplicationEntity() {

    @Transient
    var address: String? = null

    override fun getType(): ApplicationDetails.Type = ApplicationDetails.Type.HOUSE

}

