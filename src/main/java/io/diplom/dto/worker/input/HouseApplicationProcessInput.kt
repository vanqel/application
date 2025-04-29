package io.diplom.dto.worker.input

import io.diplom.models.UserEntity
import io.diplom.models.application.policy.HouseApplicationEntity

data class HouseApplicationProcessInput(

    /** Стоимость отделки */
    val finishingCost: Double? = null,

    /** Стоимость конструктивные элементы */
    val structuralElCost: Double? = null,

    /** Стоимость гражданской ответственности */
    val neighborsCost: Double? = null,

    /** Стоимость домашнего имущества */
    val household: Double? = null,

    /** Оплата в месяц */
    val monthCost: Double? = null

){

    fun processEntity(entity: HouseApplicationEntity): HouseApplicationEntity =
        entity.apply {
            this.finishingCost = finishingCost
            this.structuralElCost = structuralElCost
            this.neighborsCost = neighborsCost
            this.household = household
            this.monthCost = monthCost
        }
}
