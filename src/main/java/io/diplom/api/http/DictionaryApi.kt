package io.diplom.api.http

import io.diplom.services.dictionary.CarService
import io.diplom.services.dictionary.DadataService
import io.quarkus.vertx.web.Param
import io.quarkus.vertx.web.Route
import io.quarkus.vertx.web.RouteBase
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.MediaType

@ApplicationScoped
@RouteBase(path = "dictionary")
class DictionaryApi (
    val dadataService: DadataService,
    val carService: CarService
){
    /**
     * Автомобиля по входным данным
     */
    @Route(
        path = "/car/search",
        methods = [Route.HttpMethod.GET],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun searchCar(
        @Param search : String
    ) = carService.findCar(search)

    /**
     * Автомобиля по ID
     */
    @Route(
        path = "/car/id",
        methods = [Route.HttpMethod.GET],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun searchCarById(
        @Param id : Long?
    ) = carService.findCarById(id!!)

    /**
     * Поиск адреса по входным данным
     */
    @Route(
        path = "/dadata/search",
        methods = [Route.HttpMethod.GET],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun searchAddress(
        @Param search : String
    ) = dadataService.findAddress(search)

    /**
     * Поиск адреса по ФИАС коду
     */
    @Route(
        path = "/dadata/fias",
        methods = [Route.HttpMethod.GET],
        consumes = [MediaType.APPLICATION_JSON]
    )
    fun searchAddressByFias(
        @Param fias : String
    ) = dadataService.findAddressByFias(fias)



}
