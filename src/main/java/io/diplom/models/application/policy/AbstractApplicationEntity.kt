package io.diplom.models.application.policy

import io.diplom.models.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import java.time.LocalDateTime
import java.util.*

@MappedSuperclass
abstract class AbstractApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

    @ManyToOne
    @JoinColumn(nullable = false)
    var person: UserEntity? = null

    @Column(name = "start_date", nullable = false)
    var startDate: LocalDateTime = LocalDateTime.now()

    @Enumerated(value = EnumType.STRING)
    @Column(name = "periodic", nullable = false)
    var periodic: Periodic? = null

    @Column(name = "end_date", nullable = false)
    var endDate: LocalDateTime = periodic!!.calculate(startDate)

    @Column(name = "cost", nullable = false)
    var cost: Double? = null

    @ManyToOne
    var details: ApplicationDetails = ApplicationDetails(getType())

    abstract fun getType(): ApplicationDetails.Type

    enum class Periodic(var months: Long) {

        ONE_MONTH(1),
        SIX_MONTH(6),
        YEAR(12);

        fun calculate(date: LocalDateTime) =
            date.plusMonths(this.months)
    }
}
