package io.diplom.services.application.policy

import io.diplom.models.application.payment.PaymentEntity
import io.diplom.models.application.policy.AbstractApplicationEntity
import io.diplom.models.application.policy.ApplicationDetails
import io.smallrye.mutiny.Uni
import java.util.UUID

interface PolicyService<POLICY : AbstractApplicationEntity, INPUT : Any, PROCESS : Any> {

    fun processApplication(id: UUID, obj: PROCESS, status: ApplicationDetails.Statuses): Uni<POLICY>

    fun registerApplication(obj: INPUT): Uni<POLICY>

    fun policyForUser(): Uni<List<POLICY>>

    fun deleteApplication(id: UUID): Uni<Boolean>

}
