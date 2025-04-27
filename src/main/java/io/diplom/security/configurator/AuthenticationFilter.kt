package io.diplom.security.configurator

import io.diplom.security.models.User
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.RoutingContext

interface AuthenticationFilter {

    fun authenticate(context: RoutingContext): Uni<User>?

}
