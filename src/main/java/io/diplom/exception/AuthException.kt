package io.diplom.exception

/**
 * Ошибка авторизации
 */
class AuthException(
    prefix: String = "",
) : GeneralException(MESSAGE + prefix, 401) {

    companion object {
        const val MESSAGE = "Не авторизированный запрос к "

        fun throwable(e: Throwable): AuthException {
            System.err.println(e)
            return AuthException()
        }
    }
}
