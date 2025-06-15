package io.diplom.dto.policy.input

import io.diplom.models.application.policy.AbstractApplicationEntity
import io.diplom.models.application.policy.AbstractApplicationEntity.Periodic

abstract class AbstractApplicationInput<T : AbstractApplicationEntity> {
    abstract val person: Long

    abstract val periodic: Periodic

}
