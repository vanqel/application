package io.diplom.security.configurator

import io.diplom.exception.AuthException
import io.diplom.security.models.User
import io.quarkus.security.identity.SecurityIdentity


fun SecurityIdentity.getUser(): User {
    if (this.isAnonymous) throw AuthException()
    else return this.principal as User
}
