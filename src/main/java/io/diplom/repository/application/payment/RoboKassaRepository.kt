package io.diplom.repository.application.payment

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.extension.flatten
import io.diplom.models.application.additional.PaymentEntity
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class RoboKassaRepository(
    val jpqlEntityManager: JpqlEntityManager
) {

    fun save(entity: PaymentEntity) =
        jpqlEntityManager.JpqlQuery().withTransaction { s ->
            s.merge(entity)
        }

    fun find(invId: Int) = jpqlEntityManager.JpqlQuery().getQuery(
        jpql {
            val payment = entity(PaymentEntity::class)
            select(payment.toExpression())
                .from(payment)
                .where(payment.path(PaymentEntity::invoiceId).eq(invId))
        }
    ).flatMap { (session, query) ->
        query.singleResult.call { s ->
            session.close()
        }
    }


    fun ok(invId: Int) = find(invId).flatMap { obj ->
        obj.status = PaymentEntity.Status.OK
        jpqlEntityManager.JpqlQuery().withTransaction { it.merge(obj) }
    }


    fun err(invId: Int) = find(invId).flatMap { obj ->
        obj.status = PaymentEntity.Status.ERR
        jpqlEntityManager.JpqlQuery().withTransaction { it.merge(obj) }
    }
}
