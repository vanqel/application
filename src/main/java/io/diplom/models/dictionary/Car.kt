package io.diplom.models.dictionary

import io.diplom.models.LongEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable

@Entity
@Table(name = "cars")
@Immutable
class Car : LongEntity() {

    @Column(name = "id_mark")
    var idMark: String? = null

    @Column(name = "mark")
    var mark: String? = null

    @Column(name = "mark_ru")
    var markRu: String? = null

    @Column(name = "ispopular")
    var ispopular: Boolean? = null

    @Column(name = "country")
    var country: String? = null

    @Column(name = "model_id")
    var modelId: String? = null

    @Column(name = "model")
    var model: String? = null

    @Column(name = "model_ru")
    var modelRu: String? = null

    @Enumerated(value = EnumType.STRING)
    @Column(name = "class")
    var classField: Class? = null

    @Column(name = "year_start")
    var yearStart: Int? = null

    @Column(name = "year_end")
    var yearEnd: Int? = null

    @Column(name = "search")
    var search: String? = null


    enum class Class(val koef: Int) {
        A(1),
        B(2),
        C(3),
        D(4),
        E(5),
        F(6),
        J(6),
        M(4),
        S(7),
    }

    fun getPrice() =
        (classField?.koef ?: 1) *
                (model?.length ?: 1) *
                ((yearStart ?: 2000) - 1900) *
                10000

}
