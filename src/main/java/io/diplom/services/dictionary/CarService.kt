package io.diplom.services.dictionary

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.JpqlEntityManager
import io.diplom.models.dictionary.Car
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CarService(
    val jpqlEntityManager: JpqlEntityManager
) {

    /**
     * Поиск машинки
     */
    fun findCar(input: String): Uni<Uni<List<Car?>?>?>? {

        val query = jpql {

            val car = entity(Car::class)

            select(car.toExpression())
                .from(car)
                .where(upper(car.path(Car::search)).like("%${input.uppercase()}%"))
        }

        return jpqlEntityManager.JpqlQuery().getQuery(query).map { it.resultList }
    }
}
