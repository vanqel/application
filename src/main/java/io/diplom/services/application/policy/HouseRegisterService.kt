package io.diplom.services.application.policy

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.JpqlEntityManager
import io.diplom.dto.person.input.HouseApplicationInput
import io.diplom.dto.worker.input.HouseApplicationProcessInput
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.models.application.policy.CascoApplicationEntity
import io.diplom.models.application.policy.HouseApplicationEntity
import io.diplom.repository.user.UserRepository
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class HouseRegisterService(
    val jpqlExecutor: JpqlEntityManager,
    val userRepository: UserRepository
) : PolicyService<HouseApplicationEntity, HouseApplicationInput, HouseApplicationProcessInput> {

    override fun policyForUser() =
        userRepository.getUser().flatMap { u ->
            jpqlExecutor.JpqlQuery().getQuery(
                jpql {
                    val casco = entity(HouseApplicationEntity::class)
                    select(casco.toExpression())
                        .from(casco)
                        .where(casco.path(HouseApplicationEntity::person).eq(u))
                }
            ).flatMap { query -> query.resultList }
        }

    override fun deleteApplication(id: UUID): Uni<Boolean> {
        return jpqlExecutor.JpqlQuery().getQuery(

            jpql {
                val house = entity(HouseApplicationEntity::class)
                select(house.toExpression())
                    .from(house)
                    .where(house.path(HouseApplicationEntity::id).eq(id))
            }
        ).flatMap { it.singleResult }.flatMap {
            jpqlExecutor.delete(it)
        }
    }

    override fun processApplication(
        id: UUID,
        obj: HouseApplicationProcessInput,
        status: ApplicationDetails.Statuses

    ): Uni<HouseApplicationEntity> {
        return jpqlExecutor.JpqlQuery().getQuery(
            jpql {
                val house = entity(HouseApplicationEntity::class)
                select(house.toExpression())
                    .from(house)
                    .where(house.path(HouseApplicationEntity::id).eq(id))
            }
        ).flatMap { query -> query.singleResult }
            .map { obj.processEntity(it) }
            .flatMap {
                it.details.status = status
                jpqlExecutor.save(it)
            }
    }

    override fun registerApplication(input: HouseApplicationInput): Uni<HouseApplicationEntity> {

        return userRepository.getUser()
            .flatMap { u ->

                val details = ApplicationDetails(ApplicationDetails.Type.CASCO)
                    .apply {
                        this.price = price
                        this.status = ApplicationDetails.Statuses.WAIT_PAYMENT
                    }

                val casco = input.toEntity(u)
                    .apply { this.details = details }

                jpqlExecutor.JpqlQuery().openSession().flatMap { it.merge(casco) }
            }.flatMap { e ->
                e.details.serial = "${e.details.type.name}-${(Math.random() * 500).toInt()}"
                e.details.num = "0010${e.id}${(Math.random() * 89999 + 10000).toInt()}"
                jpqlExecutor.JpqlQuery().openSession().flatMap { it.merge(e) }
            }
    }


}
