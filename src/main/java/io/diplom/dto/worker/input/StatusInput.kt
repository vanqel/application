package io.diplom.dto.worker.input

import io.diplom.models.application.payment.PaymentEntity
import io.diplom.models.application.policy.ApplicationDetails

data class StatusInput(
    val comment: String,
    val status: ApplicationDetails.Statuses
)
