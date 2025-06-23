package io.diplom.services.application.policy

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entities.entity
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.config.jpql.PaginationInput
import io.diplom.dto.file.FileOutput
import io.diplom.dto.policy.input.CascoApplicationInput
import io.diplom.dto.policy.output.CascoOutput
import io.diplom.dto.policy.output.HouseOutput
import io.diplom.dto.worker.CascoApplicationProcessInput
import io.diplom.exception.GeneralException
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.models.application.policy.CascoApplicationEntity
import io.diplom.models.dictionary.Car
import io.diplom.repository.user.UserRepository
import io.diplom.security.configurator.getUser
import io.diplom.security.models.AuthorityName
import io.diplom.services.application.files.AdditionalDocumentService
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import io.vertx.ext.web.FileUpload
import jakarta.enterprise.context.ApplicationScoped
import java.util.*

@ApplicationScoped
class CascoRegisterService(
    val jpqlExecutor: JpqlEntityManager,
    val userRepository: UserRepository,
    val files: AdditionalDocumentService,
    val securityIdentity: SecurityIdentity
) : PolicyService<CascoApplicationEntity, CascoOutput, CascoApplicationInput, CascoApplicationProcessInput> {


    companion object {
        val entity = entity(CascoApplicationEntity::class)
    }

    override fun policyForUser(): Uni<List<CascoOutput>> =
        userRepository.getUser().flatMap { u ->
            jpqlExecutor.JpqlQuery().getQuery(
                jpql {
                    val casco = entity(CascoApplicationEntity::class)
                    select(casco.toExpression())
                        .from(casco)
                        .where(casco.path(CascoApplicationEntity::person).eq(u))
                }
            ).flatMap { (session, query) ->
                query.resultList.call { s ->
                    session.close()
                }
            }
        }.flatMap(this::wrap)

    override fun deleteApplication(id: UUID): Uni<Boolean> {
        return jpqlExecutor.JpqlQuery().getQuery(

            jpql {
                val casco = entity(CascoApplicationEntity::class)
                select(casco.toExpression())
                    .from(casco)
                    .where(casco.path(CascoApplicationEntity::id).eq(id))
            }
        ).flatMap { (session, query) ->
            query.singleResult.call { s ->
                session.close()
            }
        }.flatMap {
            jpqlExecutor.delete(it)
        }
    }

    override fun lincDocs(
        id: UUID,
        docs: List<FileUpload>
    ): Uni<List<FileOutput>> = files.putObjectsByApplication(id, docs)


    override fun registerApplication(input: CascoApplicationInput): Uni<CascoOutput> {

        val user = userRepository.getUser()

        val car = jpqlExecutor.JpqlQuery().getQuery(
            jpql {
                val car = entity(Car::class)
                select(car.toExpression())
                    .from(car)
                    .where(car.path(Car::id).eq(input.car))
            }
        ).flatMap { (session, query) ->
            query.singleResult.call { s ->
                session.close()
            }
        }

        return Uni.combine().all().unis(user, car).asTuple().flatMap { tuple ->

            val u = tuple.item1
            val c = tuple.item2

            val price = c.getPrice() * 0.035

            val details = ApplicationDetails(ApplicationDetails.Type.CASCO)
                .apply {
                    this.price = price
                    this.status = ApplicationDetails.Statuses.IN_ANALYZE
                }

            val casco = input.toEntity(u, c, 1.0)
                .apply { this.details = details }

            casco.setSerialNum()


            jpqlExecutor.save(casco)
                .map { casco ->
                    casco.additionalPersons.addAll(input.additionalPerson.map { it.toEntity(casco) })
                    casco
                }.flatMap { casco ->
                    jpqlExecutor.save(casco)
                }
        }.map(CascoApplicationEntity::setSerialNum).flatMap { e ->
            jpqlExecutor.save(e as CascoApplicationEntity)
        }.flatMap(this::wrap)
    }


    override fun findById(id: UUID): Uni<CascoOutput> {
        val query = jpql {
            selectDistinct(entity)
                .from(entity)
                .where(entity.path(CascoApplicationEntity::id).eq(id))
        }

        val u = securityIdentity.getUser()

        return jpqlExecutor.JpqlQuery().getResultData(query, PaginationInput.single()).flatMap {
            val ent = it.first()
            if (!securityIdentity.hasRole(AuthorityName.USER.name) || ent.person!!.id == u.id)
                uni { ent }
            else Uni.createFrom().failure(GeneralException("Нет прав на просмотр данного полиса"))
        }.flatMap(this::wrap)

    }


    override fun wrap(entity: List<CascoApplicationEntity>): Uni<List<CascoOutput>> {
        return entity.map { e ->
            files.getObjectsByApplication(e.linkedDocs).map {
                CascoOutput(e, it)
            }
        }.let {
            Uni.combine().all().unis<HouseOutput>(it).with { it as List<CascoOutput> }
        }
    }


    override fun wrap(entity: CascoApplicationEntity): Uni<CascoOutput> {
        return files.getObjectsByApplication(entity.linkedDocs).map {
            CascoOutput(entity, it)
        }
    }

    override fun processApplication(
        id: UUID,
        obj: CascoApplicationProcessInput,
        status: ApplicationDetails.Statuses
    ): Uni<CascoOutput> {
        return jpqlExecutor.JpqlQuery().getQuery(
            jpql {
                select(entity)
                    .from(entity)
                    .where(entity.path(CascoApplicationEntity::id).eq(id))
            }
        ).flatMap { (session, query) ->
            query.singleResult.call { s ->
                session.close()
            }
        }.map { obj.processEntity(it) }
            .flatMap {
                it.details.status = status
                jpqlExecutor.save(it)
            }.flatMap(this::wrap)
    }

}
