package io.diplom.clients.dadata

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DadataDto(

    @JsonProperty(value = "result")
    val result: String,

    @JsonProperty(value = "house_fias_id")
    val houseFiasId: String?,

    @JsonProperty(value = "house_kladr_id")
    val houseKladrId: String?,

    @JsonProperty(value = "flat_fias_id")
    val flatFiasId: String?,

    @JsonProperty(value = "flat_cadnum")
    val flatCadnum: String?,

    @JsonProperty(value = "fias_id")
    val fiasId: String,

    @JsonProperty(value = "fias_code")
    val fiasCode: String,

    @JsonProperty(value = "fias_level")
    val fiasLevel: Int,
)
