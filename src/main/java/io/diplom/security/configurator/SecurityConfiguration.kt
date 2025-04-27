package io.diplom.security.configurator

/**
 * Конфиурация безопасности. Выполняющая маршрутизацию запросов на фильтры.
 */
class SecurityConfiguration {

    companion object {
        val PERMIT_ALL = object : AuthOrder {
            override val order: Int = -1
        }

        val DENY_ALL = object : AuthOrder {
            override val order: Int = -2
        }
    }

    private enum class StateOther {
        DENY, PERMIT, AUTH;
    }

    /**
     * Настройка разрешений для неуказаных маршрутов.
     */
    private val defaultState: StateOther

    /**
     * Приватный конструктор. Для создания экземпляра класса необходимо использовать билдер.
     */
    private constructor(
        paths: List<SecurityProperty>,
        filters: MutableMap<AuthOrder, AuthenticationFilter>,
        defaultState: StateOther?
    ) {

        this.requestMatchers =
            paths.map { it.uri.count { it == '/' } to it }.sortedByDescending { it.first }.map { it.second }.toList()
        this.filters = filters

        /**
         * Проверяем корректность настроек
         */

        this.defaultState = defaultState ?: StateOther.PERMIT

        validate()
    }

    /**
     * Отрицательный порядок, при котором все запросы будут доступны без авторизации
     */
    private val permitAll = -1

    /**
     * Список зарегистированных маршрутов.
     */
    private val requestMatchers: List<SecurityProperty>

    /**
     * Список зарегистированных фильтров.
     */
    private val filters: Map<AuthOrder, AuthenticationFilter>


    /**
     * Проверяем, что все фильтры указаны
     */
    private fun validate() {

        val defaultList = listOf(PERMIT_ALL, DENY_ALL)
        val types = filters.keys
        requestMatchers
            .flatMap { it.type }
            .distinct()
            .forEach { type ->
                if (!defaultList.contains(type)) {
                    when {

                        (type.order < 0) ->
                            throw IllegalArgumentException("Неверный порядок сортироки у $${type.javaClass.simpleName}")

                        (!types.contains(type)) ->
                            throw IllegalArgumentException("Незарегистированный тип $${type.javaClass.simpleName}")

                    }
                }

            }
    }

    private data class Matcher(
        val uri: String,
        val path: SecurityProperty?,
    )

    /**
     * Возвращает список типов фильтров для данного пути
     */
    private fun match(uri: String): Matcher {
        return requestMatchers.firstOrNull { path ->
            val cleanUri = uri.indexOfLast { it == '?' }.let {
                uri.substring(0, if (it == -1) uri.length else it)
            }

            when {
                path.uri.endsWith("*") && cleanUri.startsWith(path.uri.substring(0, path.uri.length - 2)) -> true
                cleanUri == path.uri -> true
                else -> false
            }
        }?.let { prop ->
            Matcher(uri, prop)
        } ?: run { Matcher(uri, SecurityProperty(uri, otherAuth())) }
    }

    private fun otherAuth(): List<AuthOrder> =
        when (defaultState) {
            StateOther.DENY -> throw IllegalStateException("Доступ к остальным запросам запрещен")
            StateOther.PERMIT -> emptyList()
            StateOther.AUTH -> filters.keys.toList()
        }


    /**
     * Возвращает список фильтров для данного пути
     */
    fun getHandlers(uri: String): RequestProperties? {
        val def = match(uri)


        return def.path?.let { prop ->
            val types = prop.type
            val filters = if (types.any { it.order == permitAll } || types.isEmpty()) return null
            else types.map { filters[it] ?: throw IllegalStateException("Не найден фильтр с типом ${it}") }
            RequestProperties(filters, prop.rolesAllowed)
        }


    }

    /**
     * Конфигуратор безопасности.
     */
    object Builder {

        private var defaultState: StateOther? = null

        /**
         * Список маршрутов.
         */
        private val paths: MutableList<SecurityProperty> = mutableListOf()

        /**
         * Карта зарегистрированных фильтров.
         */
        private val filters: MutableMap<AuthOrder, AuthenticationFilter> = mutableMapOf()

        /**
         * Список ролей авторизованных пользователей
         */
        private val roles: MutableList<String> = mutableListOf()

        /**
         * Список ролей авторизованных пользователей
         */
        private val defaultRoles: MutableMap<AuthOrder, List<String>> = mutableMapOf()

        private fun checkState() {
            if (defaultState != null) throw IllegalStateException("Доступы к остальным запросам уже заданы")
        }

        /**
         * Неуказанные маршруты разрешены с авторизацией.
         */
        fun anyRequestAuthorized(): Builder = this.apply {
            checkState()
            defaultState = StateOther.AUTH
            authorized("/*", filters.map { it.key })
        }

        /**
         * Неуказанные маршруты разрешены для всех.
         */
        fun anyRequestPermitAll(): Builder = this.apply {
            checkState()
            defaultState = StateOther.PERMIT
            permitAll("/*")
        }

        /**
         * Неуказанные маршруты запрещены для всех.
         */
        fun anyRequestDenyAll(): Builder = this.apply {
            checkState()
            defaultState = StateOther.DENY
            authorized("/*", listOf(DENY_ALL))
        }

        /**
         * Не требует авторизации.
         */
        fun permitAll(vararg uri: String): Builder = this.apply {
            uri.forEach {
                paths.add(SecurityProperty(it, listOf(PERMIT_ALL)))
            }
        }

        /**
         * Авторизованные запросы.
         * @param order тип фильтра. Порядок обработки фильтров определяется порядком элементов в списке.
         */
        fun authorized(uri: String, order: List<AuthOrder> = emptyList(), roles: List<String> = emptyList()): Builder =
            this.apply {
                val orders = order.ifEmpty { filters.keys.toList() }

                val rolesList = orders.flatMap {
                    roles.toMutableList().apply {
                        this.addAll(defaultRoles[it] ?: emptyList())
                    }
                }

                paths.add(SecurityProperty(uri, orders, rolesList))
            }

        /**
         * Зарегистировать фильтр с его порядком.
         */
        fun <T : AuthenticationFilter> addFilter(
            filter: T,
            order: AuthOrder,
            rolesAllowed: List<String> = emptyList()
        ): Builder = this.apply {
            require(order.order > 0) { "Порядок фильтра ${order::class.simpleName} должен быть больше нуля" }
            require(!filters.containsValue(filter) and !filters.keys.contains(order)) {
                "Фильтр  ${filter::class.simpleName} и порядок фильтра ${order::class.simpleName} уже зарегистрированы"
            }
            filters[order] = filter
            defaultRoles[order] = rolesAllowed
        }

        /**
         * Создать конфигурационный класс безопасности.
         */
        fun build(): SecurityConfiguration =
            SecurityConfiguration(
                paths = paths.sortedBy { it.uri.length },
                filters = filters,
                defaultState = defaultState
            )
    }

    /**
     * Описание безопасности пути
     */
    private class SecurityProperty(
        val uri: String,
        val type: List<AuthOrder>,
        val rolesAllowed: List<String> = emptyList(),
    )
}


