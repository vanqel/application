package io.diplom.dto.worker

import io.diplom.models.application.policy.ApplicationDetails

data class StatusInput(
    val comment: String,
    val status: ApplicationDetails.Statuses
)
