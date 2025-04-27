package io.diplom.dto.person.input

import io.diplom.models.UserEntity
import io.diplom.models.application.policy.AbstractApplicationEntity
import io.diplom.models.application.policy.HouseApplicationEntity

data class HouseApplicationInput(
    override val person: Long,
    override val periodic: AbstractApplicationEntity.Periodic,
    val fiasAddress: String
) : AbstractApplicationInput<HouseApplicationEntity>() {

    fun toEntity(person: UserEntity): HouseApplicationEntity =
        HouseApplicationEntity(
            fiasAddress = fiasAddress,
        ).apply { this.person = person }


}
