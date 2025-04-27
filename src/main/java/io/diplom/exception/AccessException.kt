package io.diplom.exception

/**
 * Ошибка авторизации
 */
class AccessException : GeneralException(MESSAGE, 401) {

    companion object {
        const val MESSAGE = "Недоступно для данного пользователя"

        fun throwable(e: Throwable): AccessException {
            System.err.println(e)
            return AccessException()
        }
    }
}
