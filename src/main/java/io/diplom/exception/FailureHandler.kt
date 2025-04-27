package io.diplom.exception

import io.quarkus.vertx.web.Route
import io.vertx.core.json.JsonObject
import io.vertx.mutiny.core.http.HttpServerResponse
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType
import java.util.logging.Logger

/**
 * Обработчик ошибок. Выводит ошибку на клиентский слой в формате JSON
 */
@ApplicationScoped
class FailureHandler {

    private val logger: Logger = Logger.getLogger("exception")

    @Route(type = Route.HandlerType.FAILURE, produces = [MediaType.APPLICATION_JSON], order = Int.MAX_VALUE)
    fun failure(e: RuntimeException, exchange: HttpServerResponse) {
        ExceptionOutput.of(e).let {
            logger.info { "ERROR: ${e.stackTraceToString()}" }
            exchange.end(JsonObject.mapFrom(it).encodePrettily()).subscribe().with { }
        }
    }

}
