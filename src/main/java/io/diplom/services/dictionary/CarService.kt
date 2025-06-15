package io.diplom.services.dictionary

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.config.jpql.PaginationInput
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
    fun findCar(input: String): Uni<List<Car>> {

        val query = jpql {

            val car = entity(Car::class)

            select(car.toExpression())
                .from(car)
                .where(upper(car.path(Car::search)).like("%${input.uppercase()}%"))
        }

        return jpqlEntityManager.JpqlQuery().getResultData(query, PaginationInput(0, 1000))
    }


    /**
     * Поиск машинки
     */
    fun findCarById(id: Long): Uni<Car> {

        val query = jpql {

            val car = entity(Car::class)

            select(car.toExpression())
                .from(car)
                .where(car.path(Car::id).eq(value(id)))
        }

        return jpqlEntityManager.JpqlQuery().getResultData(query, PaginationInput(0, 1)).map { it.first() }
    }


}
