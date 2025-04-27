package io.diplom.security.filters

import io.diplom.security.configurator.AuthOrder

enum class FilterType(
    override val order: Int,
) : AuthOrder {
    INTEGRATION(1), USER(2)
}
