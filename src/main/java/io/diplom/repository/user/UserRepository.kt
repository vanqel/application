package io.diplom.repository.user

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.models.UserEntity
import io.diplom.security.configurator.getUser
import io.quarkus.security.identity.SecurityIdentity
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserRepository(
    val securityIdentity: SecurityIdentity,
    val jpqlExecutor: JpqlEntityManager
) {

    fun getUser() = jpqlExecutor.JpqlQuery().getQuery(
        jpql {
            val userEntity = entity(UserEntity::class)
            select(userEntity.toExpression())
                .from(userEntity)
                .where(userEntity.path(UserEntity::id).eq(securityIdentity.getUser().id))
        }
    ).flatMap { query -> query.singleResult }

}
