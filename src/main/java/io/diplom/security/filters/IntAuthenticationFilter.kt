package io.diplom.security.filters

import io.diplom.config.HttpConfig
import io.diplom.security.configurator.AuthenticationFilter
import io.diplom.security.models.User
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.RoutingContext
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Named
import org.eclipse.microprofile.config.inject.ConfigProperty

/**
 * Фильтр авторизации на основе JWT токена.
 */
@ApplicationScoped
@Named("developer-auth")
class IntAuthenticationFilter(
    @ConfigProperty(name = "clients.key")
    private val intKey: String,
) : AuthenticationFilter {


    /**
     * Аутентификация на основе JWT токена.
     */
    override fun authenticate(
        context: RoutingContext,
    ): Uni<User> = Uni.createFrom().context { c ->

        val tokenHeader = context.request().headers()[HttpConfig.INT_HEADER]

        return@context tokenHeader?.let {
            if (it == intKey) Uni.createFrom().item(User.empty())
            else Uni.createFrom().item { null }
        } ?: Uni.createFrom().item { null }
    }

}
