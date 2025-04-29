package io.diplom.repository.application.workers

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.JpqlEntityManager
import io.diplom.models.application.policy.ApplicationDetails
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class WorkerRepository(
    val jpqlEntityManager: JpqlEntityManager
) {


    fun takeForAnalyze(detailsId: Long) = jpqlEntityManager.JpqlQuery().getQuery(
        jpql {
            val details = entity(ApplicationDetails::class)
            select(details.toExpression())
                .from(details)
                .where(details.path(ApplicationDetails::id).eq(detailsId))
        }
    ).flatMap { query -> query.singleResult }
        .flatMap { d ->
            d.status = ApplicationDetails.Statuses.IN_ANALYZE
            jpqlEntityManager.save(d)
        }


    fun getListForWorker(type: ApplicationDetails.Type) =
        jpqlEntityManager.JpqlQuery().getQuery(
            jpql {
                val details = entity(ApplicationDetails::class)
                select(details.toExpression())
                    .from(details)
                    .where(
                        details.path(ApplicationDetails::type).eq(type).and(
                            details.path(ApplicationDetails::status).notIn(
                                ApplicationDetails.Statuses.SUCCESS,
                                ApplicationDetails.Statuses.WAIT_PAYMENT,
                                ApplicationDetails.Statuses.BREAK,
                                ApplicationDetails.Statuses.EXPIRED,
                            )
                        )
                    )
            }
        ).flatMap { query -> query.resultList }
}
