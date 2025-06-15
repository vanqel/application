package io.diplom.security.filters

import io.diplom.exception.AuthException
import io.diplom.security.configurator.AuthenticationFilter
import io.diplom.security.models.User
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.RoutingContext
import io.vertx.mutiny.core.Vertx
import io.vertx.mutiny.ext.web.client.WebClient
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Named
import jakarta.ws.rs.core.HttpHeaders
import org.eclipse.microprofile.config.inject.ConfigProperty

/**
 * Фильтр авторизации на основе JWT токена.
 */
@ApplicationScoped
@Named("jwt-auth")
class JwtAuthenticationFilter(
    vertx: Vertx,
    @ConfigProperty(name = "clients.auth")
    private val authUrl: String,
) : AuthenticationFilter {

    private val webClient = WebClient.create(vertx)

    /**
     * Аутентификация на основе JWT токена.
     */
    override fun authenticate(
        context: RoutingContext,
    ): Uni<User> = Uni.createFrom().context { c ->

        val tokenHeader = context.request().headers()[HttpHeaders.AUTHORIZATION]
        val tokenCookie = context.request().cookies().firstOrNull { it.name == COOKIE_NAME }?.value


        val token = (tokenHeader ?: tokenCookie?.let {
            PREFIX_VALUE + it
        }) ?: return@context Uni.createFrom().failure(AuthException())


        return@context webClient.getAbs("$authUrl/api/user/me").putHeader(
            HttpHeaders.AUTHORIZATION, token
        ).send().map {
            it.bodyAsJson(User::class.java)
        }.onFailure().recoverWithNull()
    }

    companion object {
        const val COOKIE_NAME = "access_token"
        const val HEADER_NAME = "Authorization"
        const val PREFIX_VALUE = "Bearer "
    }
}

