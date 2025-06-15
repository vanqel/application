package io.diplom.security.configurator

import io.diplom.exception.AuthException
import io.diplom.extension.multiFromIterable
import io.diplom.extension.toPublisher
import io.diplom.security.models.AuthorityName
import io.diplom.security.models.User
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.quarkus.security.identity.IdentityProviderManager
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.quarkus.vertx.http.runtime.security.ChallengeData
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism
import io.smallrye.mutiny.Uni
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped

/**
 * Провайдер аутентификации пользователя, выполняет проверку и аутентификацию пользователя в зависимости от [SecurityConfiguration]
 */
@ApplicationScoped
class AuthenticationProvider(
    val config: SecurityConfiguration,
) : HttpAuthenticationMechanism {


    private val logger = LoggerFactory.getLogger("auth")

    /**
     * Авторизация пользователя в зависимости от конфигурации [SecurityConfiguration]
     */
    override fun authenticate(
        context: RoutingContext,
        identityProviderManager: IdentityProviderManager?
    ): Uni<SecurityIdentity> {

        val handlers = config.getHandlers(context.request().uri())
        // если не определен фильтр или условие permitAll, то считаем что доступ открыт и пользователь анонимный
            ?: return Uni.createFrom().item {
                QuarkusSecurityIdentity.builder()
                    .setPrincipal { "anonymous" }
                    .setAnonymous(true)
                    .build()
            }


        // Получаем пользователя из каждого заданного для uri фильтра
        val user = multiFromIterable(handlers.filters.mapNotNull { it.authenticate(context) })
            .flatMap {
                it.onFailure {
                    logger.info("AuthFilters", it)
                    false
                }.recoverWithItem(User.empty())
                    .toPublisher()
            }
            .collect().asList()

        return user.onFailure()
            .transform { AuthException.throwable(it) }
            .flatMap {
                it.filterNotNull().let { auths ->
                    // если фильтры не вернули пользователя, то вернем ошибку авторизации
                    if (auths.isEmpty()) Uni.createFrom().failure(AuthException(context.request().uri()))
                    // Возвращаем первый результат успешной аутентификации в зависимости от приоритета фильтра
                    else Uni.createFrom().item(auths.first())
                }
            }.flatMap { u ->
                handlers.roles.none { u.hasAuthority(AuthorityName.valueOf(it)) }.let {
                    if (!it) Uni.createFrom().failure(AuthException(context.request().uri()))
                    else Uni.createFrom().item(u)
                }
            }.map { user1 ->
                QuarkusSecurityIdentity.builder()
                    .setPrincipal(user1)
                    .addRoles(user1.roles.map { it.name.name }.toMutableSet())
                    .build()
            }
    }

    override fun getChallenge(context: RoutingContext?): Uni<ChallengeData> {
        val result = ChallengeData(
            HttpResponseStatus.UNAUTHORIZED.code(),
            HttpHeaderNames.WWW_AUTHENTICATE,
            AuthException.MESSAGE
        )
        return Uni.createFrom().item(result)
    }
}
