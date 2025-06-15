package io.diplom.dto.policy.output

import io.diplom.dto.file.FileOutput
import io.diplom.models.application.additional.ApplicationAdditionalPersons
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.models.application.policy.CascoApplicationEntity
import io.diplom.models.dictionary.Car
import java.util.*

data class CascoOutput(
    val id: UUID,
    val kbm: Double?,
    val modelAuto: Car?,
    val numAuto: String?,
    val stsNum: String?,
    val techView: String?,
    val details: ApplicationDetails,
    val additionalPersons: List<ApplicationAdditionalPersons>,
    val documents: List<FileOutput>
) {

    constructor(entity: CascoApplicationEntity, documents: List<FileOutput>) : this(
        id = entity.id!!,
        kbm = entity.kbm,
        modelAuto = entity.modelAuto,
        numAuto = entity.numAuto,
        stsNum = entity.stsNum,
        techView = entity.techView,
        details = entity.details,
        additionalPersons = entity.additionalPersons,
        documents = documents
    )
}
