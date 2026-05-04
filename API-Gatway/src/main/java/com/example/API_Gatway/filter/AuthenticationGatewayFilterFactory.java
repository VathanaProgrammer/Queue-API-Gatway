package com.example.API_Gatway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private final RouteValidator validator;
    private final WebClient.Builder webClientBuilder;

    public AuthenticationGatewayFilterFactory(RouteValidator validator, WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.validator = validator;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (validator.isSecured.test(request)) {
                if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization header"));
                }

                String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                final String token = authHeader;

                return webClientBuilder.build()
                        .get()
                        .uri("http://AUTH-SERVICE/api/auth/validate?token=" + token)
                        .retrieve()
                        .onStatus(status -> status.isError(), response -> 
                            Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access or token expired"))
                        )
                        .toBodilessEntity()
                        .flatMap(response -> chain.filter(exchange));
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}
