package io.diplom.security.configurator


data class RequestProperties(
    val filters: List<AuthenticationFilter>,
    val roles: List<String>
)
