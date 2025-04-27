package io.diplom.config

import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class JpqlConfig {

    @ApplicationScoped
    fun renderJpql() = JpqlRenderer()

    @ApplicationScoped
    fun renderContext() = JpqlRenderContext()
}
