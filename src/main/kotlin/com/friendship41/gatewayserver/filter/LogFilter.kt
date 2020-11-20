package com.friendship41.gatewayserver.filter

import com.friendship41.gatewayserver.common.logger
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.channels.Channels

@Component
class LogFilter: GlobalFilter, Ordered {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        return chain.filter(object: ServerWebExchangeDecorator(exchange) {
            override fun getRequest(): ServerHttpRequest = object:ServerHttpRequestDecorator(exchange.request) {
                override fun getBody(): Flux<DataBuffer> {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    return super.getBody().map {
                        try {
                            Channels.newChannel(byteArrayOutputStream).write(it.asByteBuffer().asReadOnlyBuffer())
                        } catch (e: IOException) { logger().error("unable to log request", e) }
                        it
                    }.doOnComplete {
                        logger().info("Req in <<< ${request.methodValue} ${request.uri} " +
                                "host ${request.remoteAddress?.hostString}" +
                                if (byteArrayOutputStream.size() > 0) {
                                    "\nbody:\n ${String(byteArrayOutputStream.toByteArray(), Charsets.UTF_8)}"
                                } else "")

                    }
                }
            }
        }).then(Mono.fromRunnable {
            logger().info("Res >>> ${exchange.response.statusCode}")
        })
    }

    override fun getOrder(): Int {
        return -1
    }
}
