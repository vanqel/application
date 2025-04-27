package io.diplom.extension

import io.smallrye.mutiny.Multi
import io.smallrye.mutiny.Uni
import java.util.concurrent.Flow

fun <T> Uni<Uni<T>>.flatten(): Uni<T> = this.flatMap { it }

fun <T> multiFromIterable(iterable: Iterable<T>): Multi<T> = Multi.createFrom().iterable(iterable)

fun <O> Multi<Uni<O>>.flatten(): Multi<O> = this.flatMap { it.convert().toPublisher() }

fun <O> Multi<O>.toPublisher(): Flow.Publisher<O> = this.convert().toPublisher()

fun <O> Uni<O>.toPublisher(): Flow.Publisher<O> = this.convert().toPublisher()

