package io.diplom.models.application.policy

import io.diplom.models.LongEntity
import io.diplom.models.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table("application_details")
class ApplicationDetails(
    @Enumerated(value = EnumType.STRING)
    val type: Type
) : LongEntity() {

    @ManyToOne
    var worker: UserEntity? = null

    @Column(name = "serial", nullable = false)
    var serial: String? = null

    @Column(name = "num", nullable = false)
    var num: String? = null

    var comment: String? = null

    var price: Double? = null

    @Enumerated(value = EnumType.STRING)
    var status: Statuses = Statuses.PENDING


    enum class Statuses {
        PENDING, IN_ANALYZE, SUCCESS, BREAK, WAIT_PAYMENT, EXPIRED
    }

    enum class Type(val description: String) {
        CASCO("КАСКО"), HOUSE("СТРАХОВАНИЕ НЕДВИЖИМОСТИ"), OSAGO("ОСАГО")
    }
}
