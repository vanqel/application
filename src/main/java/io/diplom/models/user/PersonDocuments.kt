package io.diplom.models

import com.fasterxml.jackson.annotation.JsonIgnore
import io.diplom.models.user.PersonEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDate

@Entity
@Table(name = "documents")
class PersonDocuments(

    /**
     * Данные пользователя
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.JOIN)
    @JoinColumn(name = "person_id", nullable = false)
    var personId: PersonEntity? = null,

    @Column(nullable = false)
    val serial: String? = null,

    @Column(nullable = false)
    val number: String? = null,

    @Column(nullable = false)
    val authority: String? = null,

    @Column(nullable = false)
    val dateIssue: LocalDate? = null,

    @Column(nullable = false)
    val type: DocType? = null

) : LongEntity() {
    enum class DocType {
        PASSPORT, OMS, CV
    }
}
