package io.diplom.api.http

import io.diplom.dto.person.input.CascoApplicationInput
import io.diplom.services.application.policy.CascoRegisterService
import io.quarkus.vertx.web.Body
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType
import java.util.*

@ApplicationScoped
@RouteBase(path = "api/app/policy/")
class CascoApi(
    val cascoRegisterService: CascoRegisterService
) {

    @Route(
        path = "/list",
        methods = [Route.HttpMethod.GET],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun getPolicyForUser() = cascoRegisterService.policyForUser()


    @Route(
        path = "/register",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun registerPolicy(
        @Body body: CascoApplicationInput
    ) = cascoRegisterService.registerApplication(body)


    @Route(
        path = "/delete",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun deletePolicy(
        @Param id: String?
    ) = cascoRegisterService.deleteApplication(UUID.fromString(id!!))



}
