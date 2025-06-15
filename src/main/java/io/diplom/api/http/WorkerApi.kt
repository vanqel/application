package io.diplom.api.http

import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.services.worker.WorkerService
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType

@ApplicationScoped
@RouteBase(path = "worker")
class WorkerApi(
    val workerService: WorkerService
) {

    @Route(
        path = "/list",
        methods = [Route.HttpMethod.GET],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun getListForWorker(
        @Param type: String?
    ) = workerService.getListForWorker(ApplicationDetails.Type.valueOf(type!!))


    @Route(
        path = "/take",
        methods = [Route.HttpMethod.POST],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun takeApplicationForAnalyze(
        @Param detailsId: Int?
    ) = workerService.takeApplicationForAnalyze(detailsId!!)

}
