package io.diplom.services.application.policy

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.JpqlEntityManager
import io.diplom.dto.person.input.CascoApplicationInput
import io.diplom.models.UserEntity
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.models.application.policy.CascoApplicationEntity
import io.diplom.models.dictionary.Car
import io.diplom.security.configurator.getUser
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CasccoRegisterService(
    val securityIdentity: SecurityIdentity,
    val jpqlExecutor: JpqlEntityManager
) {

    fun getListForWorker() {
        jpqlExecutor.JpqlQuery().getQuery(
            jpql {
                val casco = entity(CascoApplicationEntity::class)
                select(casco.toExpression())
                    .from(casco)
            }
        ).flatMap { query -> query.resultList }
    }

    fun registerApplication(input: CascoApplicationInput): Uni<CascoApplicationEntity> {

        val userSecurity = securityIdentity.getUser()

        val user = jpqlExecutor.JpqlQuery().getQuery(
            jpql {
                val userEntity = entity(UserEntity::class)
                select(userEntity.toExpression())
                    .from(userEntity)
                    .where(userEntity.path(UserEntity::id).eq(userSecurity.id))
            }
        ).flatMap { query -> query.singleResult }


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
        }.flatMap { e ->
            e.details.serial = "CASCO-${(Math.random() * 500).toInt()}"
            e.details.num = "0010${e.id}${(Math.random() * 89999 + 10000).toInt()}"
            jpqlExecutor.JpqlQuery().openSession().flatMap { it.merge(e) }
        }
    }
}
