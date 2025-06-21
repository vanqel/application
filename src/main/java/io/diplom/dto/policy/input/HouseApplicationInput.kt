package io.diplom.dto.policy.input

import io.diplom.models.UserEntity
import io.diplom.models.application.policy.AbstractApplicationEntity
import io.diplom.models.application.policy.HouseApplicationEntity

data class HouseApplicationInput(
    override val person: Long,
    override val periodic: AbstractApplicationEntity.Periodic,
    val fiasAddress: String,

    /** Стоимость отделки */
    val finishingCost: Double? = null,

    /** Стоимость конструктивные элементы */
    val structuralElCost: Double? = null,

    /** Стоимость гражданской ответственности */
    val neighborsCost: Double? = null,

    /** Стоимость домашнего имущества */
    val household: Double? = null,

    /** Стоимость полиса */
    @Deprecated(message = "тот кто это увидет, забудьте, мне сказали я сделал)")
    val cost: Double? = null
) : AbstractApplicationInput<HouseApplicationEntity>() {

    fun toEntity(person: UserEntity): HouseApplicationEntity =
        HouseApplicationEntity(
            fiasAddress = fiasAddress,
            finishingCost = finishingCost,
            structuralElCost = structuralElCost,
            neighborsCost = neighborsCost,
            household = household,
        ).apply {
            this.person = person
            this.cost = this@HouseApplicationInput.cost
            this.periodic = this@HouseApplicationInput.periodic
            this.endDate =  this@HouseApplicationInput.periodic.calculate(startDate)
        }


}
