package com.friendship41.gatewayserver.filter

import com.friendship41.gatewayserver.common.logger
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component

const val LOG_LEVEL_DEBUG = "DEBUG"

// custom filter를 config로 찍어낼 수 있게 하는 클래스
@Component
class LogFilter: AbstractGatewayFilterFactory<LogFilter.Config>() {
    override fun apply(config: Config?): GatewayFilter = GatewayFilter { exchange, chain ->
        if (config?.logLevel?.toUpperCase() == LOG_LEVEL_DEBUG) {
            logger().debug("is Debug mode")
        }
        chain.filter(exchange).then()
    }

    data class Config (
            var logLevel: String
    )
}
