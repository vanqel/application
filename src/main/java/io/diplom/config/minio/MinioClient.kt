package io.diplom.config.minio

import io.minio.GetObjectArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioAsyncClient
import io.minio.PutObjectArgs
import io.minio.RemoveObjectArgs
import io.smallrye.mutiny.Uni
import io.vertx.mutiny.core.Vertx

class MinioClient(
    private val delegate: MinioAsyncClient,
    val vertx: Vertx,
    val serverUrl: String
) {

    fun getObject(args: GetObjectArgs) = Uni.createFrom().future(
        delegate.getObject(args)
    )

    fun putObject(args: PutObjectArgs) = Uni.createFrom().future(
        delegate.putObject(args)
    )

    fun remObject(args: RemoveObjectArgs) = Uni.createFrom().future(
        delegate.removeObject(args)
    )

    fun getUri(args: GetPresignedObjectUrlArgs) = vertx.executeBlocking {
        delegate.getPresignedObjectUrl(args)
    }.map {
        it.replace(serverUrl, "")
    }

    fun getUrl(args: GetPresignedObjectUrlArgs) = vertx.executeBlocking {
        delegate.getPresignedObjectUrl(args)
    }


    class Builder {

        private lateinit var delegate: MinioAsyncClient
        private lateinit var vertx: Vertx
        private lateinit var serverUrl: String


        fun setVertx(vertx: Vertx): Builder {
            this.vertx = vertx
            return this
        }

        fun setUrl(host: String, port: String): Builder {
            this.serverUrl = "$host:$port"
            return this
        }

        fun setDelegate(delegate: MinioAsyncClient): Builder {
            this.delegate = delegate
            return this
        }

        fun build() = MinioClient(vertx = vertx, serverUrl = serverUrl, delegate = delegate)
    }
}
