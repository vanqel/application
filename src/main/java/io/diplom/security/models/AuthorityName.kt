package io.diplom.security.models

enum class AuthorityName {
    ADMIN,
    USER,
    WORKER;

    fun authority(): Authority = Authority(this)
}

