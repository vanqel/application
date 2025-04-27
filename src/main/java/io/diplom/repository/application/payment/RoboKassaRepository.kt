package io.diplom.repository.application.payment

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.JpqlEntityManager
import io.diplom.extension.flatten
import io.diplom.models.application.payment.PaymentEntity
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class RoboKassaRepository(
    val jpqlEntityManager: JpqlEntityManager
) {


    fun save(entity: PaymentEntity) =
        jpqlEntityManager.JpqlQuery().openSession().map { it.merge(entity) }.flatten()

    fun find(invId: Int) = jpqlEntityManager.JpqlQuery().getQuery(
        jpql {
            val payment = entity(PaymentEntity::class)
            select(payment.toExpression())
                .from(payment)
                .where(payment.path(PaymentEntity::invoiceId).eq(invId))
        }
    ).flatMap { it.singleResult }


    fun ok(invId: Int) = find(invId).flatMap { obj ->
        obj.status = PaymentEntity.Status.OK
        jpqlEntityManager.JpqlQuery().openSession().map { it.merge(obj) }
    }.flatten()


    fun err(invId: Int) = find(invId).flatMap { obj ->
        obj.status = PaymentEntity.Status.ERR
        jpqlEntityManager.JpqlQuery().openSession().map { it.merge(obj) }
    }.flatten()
}
