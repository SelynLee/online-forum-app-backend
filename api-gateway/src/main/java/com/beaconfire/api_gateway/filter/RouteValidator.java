package com.beaconfire.api_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    private static final List<String> OPEN_API_ENDPOINTS = List.of(
            "/auth/register",
            "/auth/validate",
            "/auth/login",
            "/eureka"
    );

    public final Predicate<ServerHttpRequest> isSecured = request ->
            OPEN_API_ENDPOINTS.stream().noneMatch(uri -> matchesUri(request, uri));

    private boolean matchesUri(ServerHttpRequest request, String uri) {
        return request.getURI().getPath().contains(uri);
    }
}