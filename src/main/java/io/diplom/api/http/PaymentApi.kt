package io.diplom.api.http

import io.diplom.dto.payment.PaymentInput
import io.diplom.services.application.payment.RobokassaService
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.quarkus.vertx.web.RoutingExchange
import io.vertx.core.json.JsonObject
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType

@ApplicationScoped
@RouteBase(path = "payment")
class PaymentApi(
    val robokassaService: RobokassaService
) {

    @Route(
        path = "/link",
        methods = [Route.HttpMethod.GET],
    )
    fun link(
        @Param id: Long?
    ) = robokassaService.generatePaymentLink(id!!)


    private fun wrapBodyToPaymentInput(
        ex: RoutingExchange
    ): PaymentInput {
        val responseMap = mutableMapOf<String, Any>()
        ex.context().body().asString().let {
            it.split("&")
                .forEach {
                    it.split("=")
                        .let { responseMap.put(it[0], it[1]) }
                }
        }

        val obj = JsonObject.mapFrom(responseMap)
        return obj.mapTo(PaymentInput::class.java)
    }

    @Route(
        path = "/success",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED]
    )
    fun success(
        ex: RoutingExchange
    ) = robokassaService.success(wrapBodyToPaymentInput(ex))

    @Route(
        path = "/failure",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.APPLICATION_FORM_URLENCODED]
    )
    fun failure(
        ex: RoutingExchange
    ) = robokassaService.failure(wrapBodyToPaymentInput(ex))


}
