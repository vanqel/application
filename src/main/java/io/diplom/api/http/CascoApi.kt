package io.diplom.api.http

import io.diplom.dto.file.FileOutput
import io.diplom.dto.policy.input.CascoApplicationInput
import io.diplom.services.application.policy.CascoRegisterService
import io.quarkus.vertx.web.Body
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import io.quarkus.vertx.web.RoutingExchange
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType
import java.util.*

@ApplicationScoped
@RouteBase(path = "casco")
class CascoApi(
    val cascoRegisterService: CascoRegisterService
) {

    @Route(
        path = "/list",
        methods = [Route.HttpMethod.GET],
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
        path = "/add-docs",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.MULTIPART_FORM_DATA],
    )
    fun addDocs(
        @Param id: String,
        ex: RoutingExchange
    ): Uni<List<FileOutput>> {
        return cascoRegisterService.lincDocs(UUID.fromString(id), ex.context().fileUploads())
    }


    @Route(
        path = "/delete",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun deletePolicy(
        @Param id: String?
    ) = cascoRegisterService.deleteApplication(UUID.fromString(id!!))


}
