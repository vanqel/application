package io.diplom.dto.worker

import io.diplom.models.application.policy.CascoApplicationEntity

data class CascoApplicationProcessInput(
    /** Корректировочная цена */
    val correctCost: Double? = null,

    val stsNum: String? = null,

    val techView: String? = null,

){

    fun processEntity(entity: CascoApplicationEntity): CascoApplicationEntity =
        entity.apply {
            correctCost?.let {  this.cost = correctCost }
            this.stsNum = stsNum!!
            this.techView = techView!!
        }
}
