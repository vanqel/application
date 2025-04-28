package io.diplom.services.worker

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.JpqlEntityManager
import io.diplom.models.application.policy.AbstractApplicationEntity
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.repository.user.UserRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class WorkerService(
    val jpqlExecutor: JpqlEntityManager,
    val userRepository: UserRepository
) {

    fun takeApplicationForAnalyze(detailsId: Int) {

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
