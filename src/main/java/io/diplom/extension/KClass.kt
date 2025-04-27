package io.diplom.extension

import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

/**
 * Получить поля для
 */
fun <T : Any> KClass<T>.publicClassFields(): MutableSet<String> {
    val set = mutableSetOf<String>()

    for (property in this.memberProperties) {
        if (property.visibility in listOf(
                KVisibility.PUBLIC,
            )
        ) {
            set.add(property.name)
        }
    }

    return set
}

fun <T : Any> T.convertToMap(): Map<String, Any?> {
    val map = mutableMapOf<String, Any?>()

    val kClass = this::class
    for (property in kClass.memberProperties) {
        if (property.visibility in listOf(
                KVisibility.PUBLIC,
            )
        ) {
            map[property.name] = property.getter.call(this)
        }
    }

    return map
}
