package io.diplom.services.application.files

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entities.entity
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.config.jpql.PaginationInput
import io.diplom.config.minio.MinioClient
import io.diplom.config.minio.MinioConfiguration
import io.diplom.dto.file.FileOutput
import io.diplom.models.application.policy.LinkFileApplicationEntity
import io.diplom.security.configurator.getUser
import io.diplom.services.files.MinioService
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import io.vertx.ext.web.FileUpload
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class AdditionalDocumentService(
    val jpqlEntityManager: JpqlEntityManager,
    val securityIdentity: SecurityIdentity,
    minio: MinioClient,
) {

    val service = MinioService(
        minio,
        MinioConfiguration.DOCS
    )

    companion object {
        val entity = entity(LinkFileApplicationEntity::class)
    }


    fun getObjectsByApplication(links: List<LinkFileApplicationEntity>): Uni<List<FileOutput>> {

        val unis = links.mapNotNull { link ->
            service.getObject(link.filename!!)
        }

        return if (unis.isEmpty()) uni { emptyList() }
        else Uni.combine().all().unis<FileOutput>(unis).with { it as List<FileOutput> }

    }


    fun getObjectsByApplication(applicationId: UUID): Uni<List<FileOutput>> {
        val query = jpql {
            selectDistinct(entity)
                .from(entity)
                .where(entity.path(LinkFileApplicationEntity::applicationId).eq(value(applicationId)))
        }

        return jpqlEntityManager.JpqlQuery().getResultData(query, PaginationInput(0, 100))
            .flatMap {
                val unis = it.mapNotNull { link ->
                    service.getObject(link.filename!!)
                }

                Uni.combine().all().unis<FileOutput>(unis).with { it as List<FileOutput> }
            }
    }

    fun putObjectsByApplication(applicationId: UUID, files: List<FileUpload>): Uni<List<FileOutput>> {
        val usr = securityIdentity.getUser()

        val unis = files.map { fi ->
            val name = "${UUID.randomUUID()}_${fi.fileName()}"

            val e = LinkFileApplicationEntity(
                userId = usr.id,
                filename = name,
                applicationId = applicationId
            )

            service.addObject(name, fi).call { s ->
                jpqlEntityManager.persist(e)
            }

        }

        if (unis.isEmpty()) return uni { emptyList() }
        return Uni.combine().all().unis<FileOutput>(unis).with { it as List<FileOutput> }
    }

}
