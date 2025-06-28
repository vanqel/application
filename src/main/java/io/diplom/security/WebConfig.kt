package io.diplom.security

import io.diplom.security.configurator.SecurityConfiguration
import io.diplom.security.filters.FilterType
import io.diplom.security.filters.IntAuthenticationFilter
import io.diplom.security.filters.JwtAuthenticationFilter
import io.quarkus.runtime.Startup
import jakarta.enterprise.context.ApplicationScoped

/**
 * Конфигурация безопасности.
 */
@ApplicationScoped
class WebConfig(
    /** Фильтр аторизации разработчика.*/
    val intAuthenticationFilter: IntAuthenticationFilter,

    /** Фильтр аторизации пользователя.*/
    val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

    /**
     * Создание конфигурации безопасности.
     */
    @ApplicationScoped
    @Startup
    fun config(): SecurityConfiguration =
        SecurityConfiguration.Builder
            .addFilter(jwtAuthenticationFilter, FilterType.USER)
            .addFilter(intAuthenticationFilter, FilterType.INTEGRATION)
            .permitAll("/payment/success", "/payment/failure")
            .anyRequestAuthorized()
            .build()


}
