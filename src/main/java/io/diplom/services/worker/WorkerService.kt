package io.diplom.services.worker

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.repository.user.UserRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class WorkerService(
    val jpqlExecutor: JpqlEntityManager,
    val userRepository: UserRepository
) {

    fun takeApplicationForAnalyze(detailsId: Int) = jpqlExecutor.JpqlQuery().getQuery(
        jpql {
            val details = entity(ApplicationDetails::class)
            select(details.toExpression())
                .from(details)
                .where(details.path(ApplicationDetails::id).eq(detailsId.toLong()))
        }
    ).flatMap { query -> query.singleResult }
        .flatMap { details ->
            details.status = ApplicationDetails.Statuses.IN_ANALYZE
            jpqlExecutor.save(details)
        }


    fun getListForWorker(type: ApplicationDetails.Type) =
        jpqlExecutor.JpqlQuery().getQuery(
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
