package io.diplom.services.application.policy

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.JpqlEntityManager
import io.diplom.dto.person.input.CascoApplicationInput
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.models.application.policy.CascoApplicationEntity
import io.diplom.models.dictionary.Car
import io.diplom.repository.user.UserRepository
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class CascoRegisterService(
    val jpqlExecutor: JpqlEntityManager,
    val userRepository: UserRepository
) : PolicyService<CascoApplicationEntity, CascoApplicationInput, Nothing> {

    override fun policyForUser() =
        userRepository.getUser().flatMap { u ->
            jpqlExecutor.JpqlQuery().getQuery(
                jpql {
                    val casco = entity(CascoApplicationEntity::class)
                    select(casco.toExpression())
                        .from(casco)
                        .where(casco.path(CascoApplicationEntity::person).eq(u))
                }
            ).flatMap { query -> query.resultList }
        }

    override fun deleteApplication(id: UUID): Uni<Boolean> {
        return jpqlExecutor.JpqlQuery().getQuery(

            jpql {
                val casco = entity(CascoApplicationEntity::class)
                select(casco.toExpression())
                    .from(casco)
                    .where(casco.path(CascoApplicationEntity::id).eq(id))
            }
        ).flatMap { it.singleResult }.flatMap {
            jpqlExecutor.delete(it)
        }
    }

    override fun registerApplication(input: CascoApplicationInput): Uni<CascoApplicationEntity> {

        val user = userRepository.getUser()

        val car = jpqlExecutor.JpqlQuery().getQuery(

            jpql {
                val car = entity(Car::class)
                select(car.toExpression())
                    .from(car)
                    .where(car.path(Car::id).eq(input.car))
            }
        ).flatMap { it.singleResult }

        /**
         * TODO(API)
         */
        val kbm = 1.0


        return Uni.combine().all().unis(user, car).asTuple().flatMap { tuple ->

            val u = tuple.item1
            val c = tuple.item2

            val price = c.getPrice() * 0.035

            val details = ApplicationDetails(ApplicationDetails.Type.CASCO)
                .apply {
                    this.price = price
                    this.status = ApplicationDetails.Statuses.WAIT_PAYMENT
                }

            val casco = input.toEntity(u, c, kbm)
                .apply { this.details = details }

            jpqlExecutor.JpqlQuery().openSession().flatMap { it.merge(casco) }
        }.map(CascoApplicationEntity::setSerialNum).flatMap { e ->
            jpqlExecutor.JpqlQuery().openSession().flatMap { it.merge(e as CascoApplicationEntity) }
        }
    }

    override fun processApplication(
        id: UUID,
        obj: Nothing,
        status: ApplicationDetails.Statuses
    ): Uni<CascoApplicationEntity> = throw NotImplementedError()

}
