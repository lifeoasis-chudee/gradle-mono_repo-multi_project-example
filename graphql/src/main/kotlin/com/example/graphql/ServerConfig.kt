package com.example.graphql

import net.devh.boot.grpc.client.autoconfigure.*
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Configuration


@Configuration
@ImportAutoConfiguration(
    GrpcClientAutoConfiguration::class,
    GrpcClientMetricAutoConfiguration::class,
    GrpcClientHealthAutoConfiguration::class,
    GrpcClientSecurityAutoConfiguration::class,
    GrpcClientTraceAutoConfiguration::class,
    GrpcDiscoveryClientAutoConfiguration::class,
)
class ServerConfig {

}
