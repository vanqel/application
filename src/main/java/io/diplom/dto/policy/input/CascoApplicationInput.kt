package io.diplom.dto.policy.input

import io.diplom.models.UserEntity
import io.diplom.models.application.policy.AbstractApplicationEntity
import io.diplom.models.application.policy.CascoApplicationEntity
import io.diplom.models.dictionary.Car

data class CascoApplicationInput(
    override val person: Long,
    override val periodic: AbstractApplicationEntity.Periodic,
    val num: String,
    val car: Long,
    val additionalPerson: List<PersonAdditionalInput>
) : AbstractApplicationInput<CascoApplicationEntity>() {

    fun toEntity(person: UserEntity, car: Car, kbm: Double): CascoApplicationEntity =
        CascoApplicationEntity(
            modelAuto = car,
            kbm = kbm,
            numAuto = num
        ).apply {
            this.person = person

            this.cost = -1.0
            this.periodic = this@CascoApplicationInput.periodic
            this.endDate = this@CascoApplicationInput.periodic.calculate(startDate)
        }

}
