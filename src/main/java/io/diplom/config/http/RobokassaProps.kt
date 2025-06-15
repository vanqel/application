package io.diplom.config.http

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "robokassa")
interface RobokassaProps {
    val login: String
    val password1: String
    val password2: String
    val isTest: String
}
