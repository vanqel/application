package io.diplom.services.application.policy

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entities.entity
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.config.jpql.PaginationInput
import io.diplom.dto.file.FileOutput
import io.diplom.dto.policy.input.HouseApplicationInput
import io.diplom.dto.policy.output.HouseOutput
import io.diplom.dto.worker.HouseApplicationProcessInput
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.models.application.policy.HouseApplicationEntity
import io.diplom.repository.user.UserRepository
import io.diplom.services.application.files.AdditionalDocumentService
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.FileUpload
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class HouseRegisterService(
    val jpqlExecutor: JpqlEntityManager,
    val userRepository: UserRepository,
    val files: AdditionalDocumentService
) : PolicyService<HouseApplicationEntity, HouseOutput, HouseApplicationInput, HouseApplicationProcessInput> {

    companion object {
        val entity = entity(HouseApplicationEntity::class)
    }

    override fun policyForUser(): Uni<List<HouseOutput>> =
        userRepository.getUser().flatMap { u ->
            jpqlExecutor.JpqlQuery().getQuery(
                jpql {
                    val casco = entity(HouseApplicationEntity::class)
                    select(casco.toExpression())
                        .from(casco)
                        .where(casco.path(HouseApplicationEntity::person).eq(u))
                }
            ).flatMap { query -> query.resultList }
        }.flatMap(this::wrap)

    override fun deleteApplication(id: UUID): Uni<Boolean> {
        return jpqlExecutor.JpqlQuery().getQuery(

            jpql {
                val house = entity(HouseApplicationEntity::class)
                select(house.toExpression())
                    .from(house)
                    .where(house.path(HouseApplicationEntity::id).eq(id))
            }
        ).flatMap { it.singleResult }.flatMap {
            jpqlExecutor.delete(it)
        }
    }


    override fun lincDocs(
        id: UUID,
        docs: List<FileUpload>
    ): Uni<List<FileOutput>> = files.putObjectsByApplication(id, docs)

    override fun findById(id: UUID): Uni<HouseOutput> {
        val query = jpql {
            selectDistinct(entity)
                .from(entity)
                .where(entity.path(HouseApplicationEntity::id).eq(id))
        }

        return jpqlExecutor.JpqlQuery().getResultData(query, PaginationInput.single()).flatMap(this::wrap)
            .map { it.first() }
    }

    override fun wrap(entity: List<HouseApplicationEntity>): Uni<List<HouseOutput>> {
        return entity.map { e ->
            files.getObjectsByApplication(e.linkedDocs).map {
                HouseOutput(e, it)
            }
        }.let {
            Uni.combine().all().unis<HouseOutput>(it).with { it as List<HouseOutput> }
        }
    }

    override fun wrap(entity: HouseApplicationEntity): Uni<HouseOutput> {
        return files.getObjectsByApplication(entity.linkedDocs).map {
            HouseOutput(entity, it)
        }
    }

    override fun processApplication(
        id: UUID,
        obj: HouseApplicationProcessInput,
        status: ApplicationDetails.Statuses
    ): Uni<HouseOutput> {
        return jpqlExecutor.JpqlQuery().getQuery(
            jpql {
                val house = entity(HouseApplicationEntity::class)
                select(house.toExpression())
                    .from(house)
                    .where(house.path(HouseApplicationEntity::id).eq(id))
            }
        ).flatMap { query -> query.singleResult }
            .map { obj.processEntity(it) }
            .flatMap {
                it.details.status = status
                jpqlExecutor.save(it)
            }.flatMap(this::wrap)
    }

    override fun registerApplication(input: HouseApplicationInput): Uni<HouseOutput> {

        return userRepository.getUser()
            .flatMap { u ->

                val details = ApplicationDetails(ApplicationDetails.Type.CASCO)
                    .apply {
                        this.price = price
                        this.status = ApplicationDetails.Statuses.IN_ANALYZE
                    }

                val casco = input.toEntity(u)
                    .apply { this.details = details }

                jpqlExecutor.save(casco)
            }.flatMap { e ->
                e.details.serial = "${e.details.type.name}-${(Math.random() * 500).toInt()}"
                e.details.num = "0010${e.id}${(Math.random() * 89999 + 10000).toInt()}"
                jpqlExecutor.save(e)
            }.flatMap(this::wrap)
    }


}
