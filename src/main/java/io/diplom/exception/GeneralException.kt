package io.diplom.exception

import org.jboss.resteasy.reactive.RestResponse.StatusCode


open class GeneralException @JvmOverloads constructor(
    message: String? = null,
    val httpCode: Int = StatusCode.INTERNAL_SERVER_ERROR,
    /**
     * Код ошибки
     */
    val errors: Collection<String> = listOf(),
) : RuntimeException(message) {


    override val message: String
        get() {
            return super.message ?: "Неизвестная ошибка"
        }

}
