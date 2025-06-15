package io.diplom.models.application.policy

import io.diplom.models.UserEntity
import io.diplom.models.application.additional.ApplicationAdditionalPersons
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import java.time.LocalDateTime
import java.util.*

@MappedSuperclass
sealed class AbstractApplicationEntity {

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
    var endDate: LocalDateTime? = null
        get() = periodic!!.calculate(startDate)

    @Column(name = "cost", nullable = false)
    var cost: Double? = null

    @OneToOne(orphanRemoval = true, cascade = [CascadeType.ALL])
    var details: ApplicationDetails = ApplicationDetails(getType())

    @OneToMany(orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(
        name = "application_id",
        referencedColumnName = "id"
    )
    val linkedDocs : List<LinkFileApplicationEntity> = emptyList()

    @OneToMany(fetch = FetchType.EAGER, orphanRemoval = true, cascade = [CascadeType.ALL])
    @JoinColumn(
        name = "application_id",
        referencedColumnName = "id"
    )
    val additionalPersons: MutableList<ApplicationAdditionalPersons> = mutableListOf()

    abstract fun getType(): ApplicationDetails.Type

    enum class Periodic(var months: Long) {

        ONE_MONTH(1),
        SIX_MONTH(6),
        YEAR(12);

        fun calculate(date: LocalDateTime) =
            date.plusMonths(this.months)
    }

    fun setSerialNum(): AbstractApplicationEntity {
        details.serial = "${details.type.name}-${(Math.random() * 500).toInt()}"
        details.num = "0010${id}${(Math.random() * 89999 + 10000).toInt()}"
        return this
    }
}
