package io.diplom.exception

import io.quarkus.security.UnauthorizedException
import java.time.LocalDateTime

open class ExceptionOutput(
    val message: String?,
    val statusCode: Int,
    val time: LocalDateTime = LocalDateTime.now()
) {

    constructor(g: GeneralException) : this(
        g.message, g.httpCode
    )

    constructor(e: UnauthorizedException) : this(AccessException())

    constructor(g: RuntimeException) : this(
        if (g is GeneralException) g.message
        else "Неизвестная ошибка",
        if (g is GeneralException) g.httpCode
        else 500
    )

    companion object {
        fun of(e: Throwable): ExceptionOutput {
            return when (e) {
                is GeneralException -> ExceptionOutput(e)
                is UnauthorizedException -> ExceptionOutput(e)
                else -> ExceptionOutput(RuntimeException(e))
            }
        }
    }
}
