package io.averkhoglyad.shortcut.notification.config

import io.averkhoglyad.shortcut.notification.api.NotificationSubscriptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class WebConfig(
    private val notificationSubscriptionHandler: NotificationSubscriptionHandler,
) {

    @Bean
    fun webFluxConfigurer(): WebFluxConfigurer {
        return object : WebFluxConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**").allowedOrigins("*")
            }
        }
    }

    @Bean
    fun routes(): RouterFunction<ServerResponse> {
        return route()
            .GET("/subscribe", notificationSubscriptionHandler::subscribe)
            .build()
    }
}