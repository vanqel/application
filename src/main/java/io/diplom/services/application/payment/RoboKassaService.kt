package io.diplom.services.application.payment

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import io.diplom.config.http.RobokassaProps
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.dto.payment.PaymentInput
import io.diplom.dto.policy.output.PaymentOutput
import io.diplom.exception.GeneralException
import io.diplom.models.application.additional.PaymentEntity
import io.diplom.models.application.policy.ApplicationDetails
import io.diplom.repository.application.payment.RoboKassaRepository
import io.diplom.security.configurator.getUser
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.core.UriBuilder
import java.security.MessageDigest

@ApplicationScoped
class RobokassaService(
    val props: RobokassaProps,
    val repository: RoboKassaRepository,
    val jpqlEntityManager: JpqlEntityManager,
    val securityIdentity: SecurityIdentity
) {

    fun generatePaymentLink(detailsId: Long): Uni<PaymentOutput> {

        val userSecurity = securityIdentity.getUser()

        val details = jpqlEntityManager.JpqlQuery().getQuery(
            jpql {
                val application = entity(ApplicationDetails::class)
                select(application.toExpression())
                    .from(application)
                    .where(application.path(ApplicationDetails::id).eq(detailsId))
            }
        ).flatMap { (session, query) ->
            query.singleResult.call { s ->
                session.close()
            }
        }


        return details.flatMap { turple ->

            val p = turple

            if (p.status != ApplicationDetails.Statuses.WAIT_PAYMENT)
                return@flatMap Uni.createFrom().failure(GeneralException("Заявка не одобрена для оплаты"))

            val invoiceID = (Math.random() * 100000 + 1).toInt()
            val signatureValue = generateSignatureValue(props.login, p.price!!, props.password1, invoiceID)
            val url = UriBuilder.fromPath("https://auth.robokassa.ru/Merchant/Index.aspx")
                .queryParam("MerchantLogin", props.login)
                .queryParam("OutSum", String.format("%.2f", p.price!!))
                .queryParam("Description", "${p.type.description} ${p.serial}-${p.num}")
                .queryParam("SignatureValue", signatureValue)
                .queryParam("InvoiceID", invoiceID)
                .queryParam("IsTest", props.isTest)
                .build().toURL().toString()

            PaymentEntity(
                url = url,
                invoiceId = invoiceID,
                checkSumm = signatureValue,
                applicationDetails = p,
                refer = userSecurity.id,
                cost = p.price!!
            ).let {
                repository.save(it)
            }
        }.map { it.toDTO() }
    }

    private fun generateSignatureValue(
        merchantLogin: String,
        outSum: Double,
        password: String,
        invoiceID: Int,
    ): String {
        val data = "$merchantLogin:${"%.2f".format(outSum)}:$invoiceID:$password"
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(data.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }


    private fun verify(outSum: Double, invId: Int, signatureValue: String): Int {
        val mrhPass1 = props.password1 // merchant pass1 here
        val outSumm = outSum.toString() // Convert to string
        val invIdStr = invId.toString() // Convert to string
        val param = "${outSumm}0:$invIdStr:$mrhPass1"
        val myCrc = MessageDigest.getInstance("MD5").digest(param.toByteArray())
            .joinToString("") { "%02x".format(it) }

        println("Calc CRC: $myCrc | Requested CRC: $signatureValue | PARAM-IN: $outSumm:$invIdStr:$mrhPass1 | PARAM-OUT: $param")
        print(myCrc)
        // Compare CRCs
        if (myCrc != signatureValue) {
            throw GeneralException("Ошибка проверки контрольных сумм")
        }
        return invId
    }

    fun success(body: PaymentInput) = succ(body.OutSum.toString().toDouble(), body.InvId, body.SignatureValue)
    fun failure(body: PaymentInput) = err(body.OutSum.toString().toDouble(), body.InvId, body.SignatureValue)

    private fun succ(outSum: Double, invId: Int, signatureValue: String): Uni<ApplicationDetails> =
        verify(outSum, invId, signatureValue)
            .let {
                repository.ok(invId)
            }
            .map {
                it.applicationDetails!!.status = ApplicationDetails.Statuses.SUCCESS
                it
            }
            .flatMap {
                repository.save(it)
            }.flatMap { p ->
                val details = p.applicationDetails!!
                jpqlEntityManager.JpqlQuery().withTransaction {
                    details.status = ApplicationDetails.Statuses.SUCCESS
                    it.merge(details)
                }
            }

    fun err(outSum: Double, invId: Int, signatureValue: String): Uni<ApplicationDetails> =
        verify(outSum, invId, signatureValue)
            .let {
                repository.err(invId)
            }
            .map {
                it.applicationDetails!!.status = ApplicationDetails.Statuses.WAIT_PAYMENT
                it
            }
            .flatMap {
                repository.save(it)
            }.flatMap { p ->
                val details = p.applicationDetails!!
                jpqlEntityManager.JpqlQuery().withTransaction {
                    details.status = ApplicationDetails.Statuses.WAIT_PAYMENT
                    it.merge(details)
                }
            }


}
