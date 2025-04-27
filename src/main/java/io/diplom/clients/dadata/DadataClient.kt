package io.diplom.clients.dadata

import io.smallrye.mutiny.Uni
import io.vertx.mutiny.core.Vertx
import io.vertx.mutiny.core.buffer.Buffer
import io.vertx.mutiny.ext.web.client.WebClient
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.HttpHeaders
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.util.logging.Level
import java.util.logging.Logger

@ApplicationScoped
class DadataClient(
    val vertx: Vertx,
    @ConfigProperty(name = "clients.dadata.token")
    private val token: String,

    @ConfigProperty(name = "clients.dadata.xtoken")
    private val xtoken: String,
) {

    private val webClient = WebClient.create(vertx)
    private val logger = Logger.getLogger("dadata")


    fun getAddress(input: String): Uni<Array<DadataDto>>? {

        return webClient.getAbs("https://cleaner.dadata.ru/api/v1/clean/address")
            .putHeader(HttpHeaders.AUTHORIZATION, token)
            .putHeader("X-Secret", xtoken)
            .sendBuffer(Buffer(input))
            .map {
                it.bodyAsJson(Array::class.java) as Array<DadataDto>
            }.onFailure {
                logger.log(Level.WARNING, it.message)
                true
            }.recoverWithNull()
    }


    fun getAddressByFias(input: String): Uni<DadataDto?> {

        return webClient.getAbs(" http://suggestions.dadata.ru/suggestions/api/4_1/rs/findById/address")
            .putHeader(HttpHeaders.AUTHORIZATION, token)
            .putHeader("X-Secret", xtoken)
            .sendJson(DadataQuery(input))
            .map {
                it.bodyAsJson(DadataDto::class.java)
            }.onFailure {
                logger.log(Level.WARNING, it.message)
                true
            }.recoverWithNull()
    }
}
