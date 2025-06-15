package io.diplom.services.application.policy

import io.diplom.dto.file.FileOutput
import io.diplom.models.application.policy.AbstractApplicationEntity
import io.diplom.models.application.policy.ApplicationDetails
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.FileUpload
import java.util.*

interface PolicyService<POLICY : AbstractApplicationEntity, POLICY_DTO : Any, INPUT : Any, PROCESS : Any> {

    fun findById(id: UUID): Uni<POLICY_DTO>

    fun wrap(entity: POLICY): Uni<POLICY_DTO>

    fun wrap(entity: List<POLICY>): Uni<List<POLICY_DTO>>

    fun processApplication(id: UUID, obj: PROCESS, status: ApplicationDetails.Statuses): Uni<POLICY_DTO>

    fun registerApplication(obj: INPUT): Uni<POLICY_DTO>

    fun policyForUser(): Uni<List<POLICY_DTO>>

    fun deleteApplication(id: UUID): Uni<Boolean>

    fun lincDocs(id: UUID, docs: List<FileUpload>): Uni<List<FileOutput>>

}
