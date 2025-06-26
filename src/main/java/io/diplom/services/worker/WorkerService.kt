package io.diplom.services.worker

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.exception.GeneralException
import io.diplom.models.application.policy.AbstractApplicationEntity
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.models.application.policy.CascoApplicationEntity
import io.diplom.models.application.policy.HouseApplicationEntity
import io.diplom.repository.user.UserRepository
import io.diplom.services.application.policy.CascoRegisterService
import io.diplom.services.application.policy.HouseRegisterService
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import kotlin.reflect.KClass

@ApplicationScoped
class WorkerService(
    val jpqlExecutor: JpqlEntityManager,
    val userRepository: UserRepository,
    val houseRegisterService: HouseRegisterService,
    val cascoRegisterService: CascoRegisterService
) {

    fun takeApplicationForAnalyze(detailsId: Int) = jpqlExecutor.JpqlQuery().getQuery(
        jpql {
            val details = entity(ApplicationDetails::class)
            select(details.toExpression())
                .from(details)
                .where(details.path(ApplicationDetails::id).eq(detailsId.toLong()))
        }
    ).flatMap { (session, query) ->
        query.singleResult.call { s ->
            session.close()
        }
    }
        .flatMap { details ->
            userRepository.getUser().flatMap {
                details.worker = it
                details.status = ApplicationDetails.Statuses.IN_ANALYZE
                jpqlExecutor.save(details)
            }
        }


    fun getListForWorker(type: ApplicationDetails.Type): Uni<out List<Any>> =
        when (type) {
            ApplicationDetails.Type.NOTHING -> throw GeneralException("Ошибка")
            ApplicationDetails.Type.CASCO -> getQuery(
                CascoApplicationEntity::class,
                ApplicationDetails.Type.CASCO
            ).flatMap { cascoRegisterService.wrap(it) }

            ApplicationDetails.Type.HOUSE -> getQuery(
                HouseApplicationEntity::class,
                ApplicationDetails.Type.HOUSE
            ).flatMap { houseRegisterService.wrap(it) }
        }

    private inline fun <reified T : AbstractApplicationEntity> getQuery(
        cls: KClass<T>, type: ApplicationDetails.Type
    ) = jpql {
        val details = entity(ApplicationDetails::class)
        val entity = entity(cls)

        select<T>(entity)
            .from(
                entity,
                join(entity.path(AbstractApplicationEntity::details)),
                leftFetchJoin(entity.path(AbstractApplicationEntity::linkedDocs)),
            )
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
    }.let {
        jpqlExecutor.JpqlQuery().getQuery<T>(it).flatMap { (session, query) ->
            query.resultList.call { s ->
                session.close()
            }
        }
    }

}

