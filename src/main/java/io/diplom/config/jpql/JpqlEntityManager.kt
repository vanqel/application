package io.diplom.config.jpql

import com.fasterxml.jackson.core.type.TypeReference
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRendered
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import io.diplom.extension.pagination
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.PersistenceContext
import org.hibernate.reactive.mutiny.Mutiny
import java.util.*

/**
 * DAO для работы с запросами, которые были созданы с помощью собственной библиотеки на основе JPQL
 */
@ApplicationScoped
final class JpqlEntityManager(

    val render: JpqlRenderer,
    val context: JpqlRenderContext,
    /**
     * Асинхронный менеджера сессий Hibernate-Reactive
     */
    @PersistenceContext
    val entityManager: Mutiny.SessionFactory,
) {

    fun <T : Any> save(obj: T): Uni<T> = entityManager.withTransaction { it.merge(obj) }

    fun persist(obj: Any): Uni<Void> = entityManager.withTransaction { it.persist(obj) }

    fun <T : Any> delete(obj: T): Uni<Boolean> =
        entityManager.withTransaction { it.remove(obj) }
            .map { true }


    inline fun <reified T : Any> delete(id: UUID): Uni<Int> =
        entityManager.withTransaction {
            it.createMutationQuery(
                "DELETE FROM ${T::class.java.simpleName} WHERE id = :id"
            ).setParameter("id", id).executeUpdate()
        }


    inner class JpqlQuery {

        fun openSession(): Uni<Mutiny.Session> = entityManager.openSession()

        fun <T : Any> withTransaction(query: (Mutiny.Session) -> Uni<T>): Uni<T> =
            entityManager.withTransaction { session -> query.invoke(session) }

        inline fun <reified T : Any> getQuery(
            query: SelectQuery<T>,
        ): Uni<Pair<Mutiny.Session, Mutiny.SelectionQuery<T>>> {
            val rendered = render.render(query, context)
            return getQuery(rendered)
        }

        /**
         * Получение потока данных с помощью динамически сконструированного запроса
         */
        inline fun <reified T : Any> getQuery(
            render: JpqlRendered,
        ) = entityManager.openSession().map { s ->
            val callableQuery = s.createQuery(
                render.query, T::class.java
            )

            s to callableQuery.apply { render.params.forEach { (name, value) -> setParameter(name, value) } }
        }


        inline fun <reified T : Any> getResultData(query: SelectQuery<T>, pagination: PaginationInput): Uni<List<T>> {
            val rendered = render.render(query, context)
            return getResultDataRef(rendered, pagination)
        }

        /**
         * Получение потока данных с помощью динамически сконструированного запроса
         */
        inline fun <reified T : Any> getResultDataRef(
            render: JpqlRendered,
            pagination: PaginationInput
        ): Uni<List<T>> {
            return entityManager.withSession { s ->
                val callableQuery = s.createQuery(
                    render.query, T::class.java
                )

                callableQuery
                    .apply { render.params.forEach { (name, value) -> setParameter(name, value) } }
                    .pagination(pagination)
                    .resultList
            }.map { it.filterNotNull() }

        }


    }


    inner class JpqlRender {

        /**
         * Получение потока данных с помощью динамически сконструированного запроса
         */
        fun <T> getResultData(render: JpqlRendered, pagination: PaginationInput, type: Class<T>): Uni<List<T>> {
            return entityManager.withSession { s ->
                val callableQuery = s.createQuery(
                    render.query, type
                )

                callableQuery
                    .apply { render.params.forEach { (name, value) -> setParameter(name, value) } }
                    .pagination(pagination)
                    .resultList
            }.map { it.filterNotNull() }

        }

        /**
         * Получение потока количества данных с помощью динамически сконструированного запроса
         */
        fun getResultCount(render: JpqlRendered): Uni<Long> {
            return entityManager.withSession { s ->
                val callableQuery = s.createQuery(
                    "SELECT COUNT(1) FROM ( ${render.query} )", Long::class.java
                )
                callableQuery
                    .apply { render.params.forEach { (name, value) -> setParameter(name, value) } }
                    .singleResult
            }
        }


        /**
         * Получение потока количества данных с помощью динамически сконструированного запроса
         */
        fun <T> getSingleResult(render: JpqlRendered, type: Class<T>): Uni<T> {
            return entityManager.withSession { s ->
                val callableQuery = s.createQuery(
                    render.query, type
                )
                callableQuery
                    .apply { render.params.forEach { (name, value) -> setParameter(name, value) } }
                    .singleResult
            }
        }
    }

}
