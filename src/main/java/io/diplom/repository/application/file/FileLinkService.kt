package io.diplom.repository.application.file

import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.exception.GeneralException
import io.diplom.security.configurator.getUser
import io.diplom.security.models.AuthorityName
import io.minio.MinioAsyncClient
import io.quarkus.security.identity.SecurityIdentity
import io.vertx.mutiny.ext.web.FileUpload
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class FileLinkService(
    val jpqlEntityManager: JpqlEntityManager,
    val securityIdentity: SecurityIdentity,
    val minioAsyncClient: MinioAsyncClient
) {

    fun linkDocument(applicationId: Long, file: FileUpload) {

        if (!securityIdentity.getUser().hasAuthority(AuthorityName.USER))
            throw GeneralException("Нет прав на загрузку документа")

        val uuid = UUID.randomUUID()

    }


}
