package io.diplom.dto.policy.output

import io.diplom.dto.file.FileOutput
import io.diplom.models.application.additional.ApplicationAdditionalPersons
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.models.application.policy.HouseApplicationEntity
import java.util.*

data class HouseOutput(
    val id: UUID,
    var finishingCost: Double?,
    var structuralElCost: Double?,
    var neighborsCost: Double?,
    var household: Double?,
    var monthCost: Double?,
    var fiasAddress: String?,
    var egrn: String?,
    val details: ApplicationDetails,
    val additionalPersons: List<ApplicationAdditionalPersons>,
    val documents: List<FileOutput>
) {

    constructor(entity: HouseApplicationEntity, documents: List<FileOutput>) : this(
        id = entity.id!!,
        finishingCost = entity.finishingCost,
        structuralElCost = entity.structuralElCost,
        neighborsCost = entity.neighborsCost,
        household = entity.household,
        monthCost = entity.monthCost,
        fiasAddress = entity.fiasAddress,
        egrn = entity.egrn,
        details = entity.details,
        additionalPersons = entity.additionalPersons,
        documents = documents
    )
}
