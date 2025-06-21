package io.diplom.models.application.additional

import io.diplom.dto.policy.output.PaymentOutput
import io.diplom.models.LongEntity
import io.diplom.models.UserEntity
import io.diplom.models.application.policy.ApplicationDetails
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "payment")
class PaymentEntity(

    @Column(name = "refer", nullable = false)
    val refer: Long? = null,

    @Column(name = "checksumm", nullable = false)
    val checkSumm: String? = null,

    @ManyToOne
    @JoinColumn(name = "detailsId", nullable = false)
    val applicationDetails: ApplicationDetails? = null,

    @Column(name = "cost", nullable = false)
    val cost: Double? = null,

    @Column(name = "inv_id", nullable = false)
    val invoiceId: Int? = null,

    @Column(name = "url", nullable = false)
    var url: String? = null,

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: Status = Status.WAIT

) : LongEntity() {
    enum class Status {
        OK, ERR, WAIT
    }

    fun toDTO() = PaymentOutput(
        refer!!,
        applicationDetails!!,
        cost!!,
        invoiceId!!,
        url!!,
        status
    )
}
