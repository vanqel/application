package io.diplom.dto.policy.output

import io.diplom.models.UserEntity
import io.diplom.models.application.additional.PaymentEntity
import io.diplom.models.application.policy.ApplicationDetails

class PaymentOutput(

    val refer: UserEntity,

    val applicationDetails: ApplicationDetails,

    val cost: Double,

    val invoiceId: Int,

    var url: String,

    var status: PaymentEntity.Status = PaymentEntity.Status.WAIT

)
