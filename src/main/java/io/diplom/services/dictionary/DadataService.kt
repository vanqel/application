package io.diplom.services.dictionary

import io.diplom.clients.dadata.DadataClient
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class DadataService(
    val dadataClient: DadataClient
) {
    /**
     * Поиск адреса по подсказкам
     */
    fun findAddress(input: String) = dadataClient.getAddress(input)

    /**
     * Поиск адреса по фиас
     */
    fun findAddressByFias(input: String) = dadataClient.getAddressByFias(input)
}
