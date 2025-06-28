package io.diplom.dto.payment

data class PaymentInput(
    val OutSum: Any,
    val InvId: Int,
    val SignatureValue:String,
    val IsTest: Int,
    val Culture: String
)
