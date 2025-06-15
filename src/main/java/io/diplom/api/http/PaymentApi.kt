package io.diplom.api.http

import io.diplom.dto.payment.PaymentInput
import io.diplom.services.application.payment.RobokassaService
import io.quarkus.vertx.web.Body
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType

@ApplicationScoped
@RouteBase(path = "payment")
class PaymentApi(
    val robokassaService: RobokassaService
) {

    @Route(
        path = "/link",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun link(
        @Param id: Long?
    ) = robokassaService.generatePaymentLink(id!!)


    @Route(
        path = "/success",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun success(
        @Body body: PaymentInput
    ) = robokassaService.success(body)

    @Route(
        path = "/failure",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun failure(
        @Body body: PaymentInput
    ) = robokassaService.failure(body)


}
