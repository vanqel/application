package io.diplom.api.http

import io.diplom.dto.file.FileOutput
import io.diplom.dto.policy.input.HouseApplicationInput
import io.diplom.dto.worker.HouseApplicationProcessInput
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.services.application.policy.HouseRegisterService
import io.quarkus.vertx.web.Body
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.quarkus.vertx.web.RoutingExchange
import io.smallrye.mutiny.Uni
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType
import java.util.*

@ApplicationScoped
@RouteBase(path = "house")
class HouseApi(
    val houseRegisterService: HouseRegisterService
) {

    @Route(
        path = "/list",
        methods = [Route.HttpMethod.GET],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun getPolicyForUser() = houseRegisterService.policyForUser()


    @Route(
        path = "/register",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun registerPolicy(
        @Body body: HouseApplicationInput
    ) = houseRegisterService.registerApplication(body)


    @Route(
        path = "/delete",
        methods = [Route.HttpMethod.DELETE],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun deletePolicy(
        @Param id: String?
    ) = houseRegisterService.deleteApplication(UUID.fromString(id!!))


    @RolesAllowed("WORKER", "ADMIN")
    @Route(
        path = "/process",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun processPolicy(
        @Param id: String?,
        @Param status: String?,
        @Body body: HouseApplicationProcessInput
    ) = houseRegisterService.processApplication(UUID.fromString(id!!), body, ApplicationDetails.Statuses.valueOf(status!!))


    @Route(
        path = "/add-docs",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.MULTIPART_FORM_DATA],
    )
    fun addDocs(
        @Param id: String,
        ex: RoutingExchange
    ): Uni<List<FileOutput>> {
        return houseRegisterService.lincDocs(UUID.fromString(id), ex.context().fileUploads())
    }

}
