package com.friendship41.gatewayserver.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.friendship41.gatewayserver.common.logger
import io.jsonwebtoken.*
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.annotation.PostConstruct

const val BEARER = "BEARER "

@Component
class JwtTokenProvider {
    lateinit var publicKey: PublicKey

    @Value("\${auth-server.token_key.url}")
    private val tokenKeyUrl: String? = null

    @PostConstruct
    fun getPublicKeyFromAuthServer() {
        val tempRestTemplate = RestTemplate()
        val response = tempRestTemplate.getForObject(
                this.tokenKeyUrl
                        ?: throw RuntimeException("[getPublicKeyFromAuthServer] application properties 'tokenKeyUrl' not exist"),
                String::class.java)
        val parser = jacksonObjectMapper().readValue(response, Map::class.java)
        this.publicKey = this.createPublicKey(
                parser["value"].toString(),
                parser["alg"].toString())
        logger().info("Success to get public key from auth server")
        logger().info("public key : "+this.publicKey)
    }

    fun validateJwt(jwtToken: String): Jws<Claims> = try {
        Jwts.parserBuilder()
                .setSigningKey(this.publicKey)
                .build()
                .parseClaimsJws(jwtToken)
    } catch (e: SignatureException) {
        logger().error("Invalid JWT signature: $jwtToken")
        throw e
//        throw BadCredentialsException("Invalid JWT signature: $jwtToken")
    } catch (e: MalformedJwtException) {
        logger().error("Invalid token: $jwtToken")
        throw e
//        throw BadCredentialsException("Invalid token: $jwtToken")
    } catch (e: ExpiredJwtException) {
        logger().error("Expired JWT token: $jwtToken")
        throw e
//        throw BadCredentialsException("Expired JWT token: $jwtToken")
    } catch (e: UnsupportedJwtException) {
        logger().error("Unsupported JWT token: $jwtToken")
        throw e
//        throw BadCredentialsException("Unsupported JWT token: $jwtToken")
    } catch (e: IllegalArgumentException) {
        logger().error("JWT token compact of handler are invalid: $jwtToken")
        throw e
//        throw BadCredentialsException("JWT token compact of handler are invalid: $jwtToken")
    } catch (e: Exception) {
        logger().error("Invalid token: $jwtToken")
        throw e
//        throw BadCredentialsException("Invalid token: $jwtToken")
    }

    private fun createPublicKey(base64EncodedPublicKey: String, algorithm: String): PublicKey =
            KeyFactory.getInstance(algorithm)
                    .generatePublic(X509EncodedKeySpec(
                            Base64.getDecoder().decode(base64EncodedPublicKey)))


    fun getTokenWithValidation(headerToken: String): String? {
        if (headerToken.substring(0, BEARER.length).toUpperCase() != BEARER) {
            return null
        }
        return headerToken.substring(BEARER.length)
    }
}
