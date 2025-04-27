package io.diplom.models.application.payment

import io.diplom.dto.person.output.PaymentOutput
import io.diplom.models.LongEntity
import io.diplom.models.UserEntity
import io.diplom.models.application.policy.ApplicationDetails
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.ManyToOne

@Entity
class PaymentEntity(

    @Column(name = "refer")
    @ManyToOne
    val refer: UserEntity,

    @Column(name = "checksumm")
    val checkSumm: String,

    @ManyToOne
    @Column(name = "detailsId")
    val applicationDetails: ApplicationDetails,

    @Column(name = "cost")
    val cost: Double,

    @Column(name = "inv_id")
    val invoiceId: Int,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "url")
    var url: String,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    var status: Status = Status.WAIT

) : LongEntity() {
    enum class Status {
        OK, ERR, WAIT
    }

    fun toDTO() = PaymentOutput(
        refer,
        applicationDetails,
        cost,
        invoiceId,
        url,
        status
    )
}
