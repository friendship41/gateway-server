package com.friendship41.gatewayserver.filter

import com.friendship41.gatewayserver.auth.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher

@Component
class JwtAuthGatewayFilterFactory(@Autowired private val jwtTokenProvider: JwtTokenProvider)
    : AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config>(Config::class.java) {
    val antPathMatcher = AntPathMatcher()

    override fun apply(config: Config?): GatewayFilter = GatewayFilter { exchange, chain ->
        var isExclude: Boolean = false
        config?.excludePaths?.stream()
                ?.filter { antPathMatcher.match(it, exchange.request.path.toString()) }
                ?.forEach { isExclude = true }

        if (!isExclude) {
            if (!exchange.request.headers.containsKey("Authorization")) {
                throw Exception("Auth Header not found!")
            }
            val jwtToken = this.jwtTokenProvider.getTokenWithValidation(
                    exchange.request.headers.getFirst("Authorization")
                            ?: throw Exception("no auth header value!")) ?: throw Exception("no auth header value!")
            val jws = this.jwtTokenProvider.validateJwt(jwtToken)
            exchange.request.mutate().headers {
                it.set("memberNo", jws.body["memberNo"].toString())
            }
        }

        chain.filter(exchange)
    }

    data class Config (
            var excludePaths: List<String>?
    )
}
