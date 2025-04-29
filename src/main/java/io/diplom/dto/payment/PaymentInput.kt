package io.diplom.dto.payment

data class PaymentInput(
    val OutSum: Double,
    val InvId: Int,
    val SignatureValue:String
)
