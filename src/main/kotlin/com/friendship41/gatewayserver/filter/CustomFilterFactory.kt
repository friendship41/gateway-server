package com.friendship41.gatewayserver.filter

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component

// custom filter를 config로 찍어낼 수 있게 하는 클래스
@Component
class CustomFilterFactory: AbstractGatewayFilterFactory<CustomFilterFactory.Config>() {
    override fun apply(config: Config?): GatewayFilter = GatewayFilter { exchange, chain ->
        chain.filter(exchange).then()
    }

    data class Config (
            var logLevel: String
    )
}
