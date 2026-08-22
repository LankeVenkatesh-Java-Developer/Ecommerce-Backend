package com.ashok.it.userservice.Security;

import com.ashok.it.userservice.Service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
@RequiredArgsConstructor
@Order(1)
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        // No JWT → continue
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            if (email != null) {
                return ReactiveSecurityContextHolder.getContext()
                        .flatMap(context -> {
                            if (context.getAuthentication() == null) {
                                if (jwtService.isTokenValid(token, email)) {
                                    UsernamePasswordAuthenticationToken authentication =
                                            new UsernamePasswordAuthenticationToken(
                                                    email,
                                                    null,
                                                    Collections.singletonList(
                                                            new SimpleGrantedAuthority("ROLE_USER")
                                                    )
                                            );

                                    return chain.filter(exchange)
                                            .contextWrite(ReactiveSecurityContextHolder
                                                    .withAuthentication(authentication));
                                }
                            }
                            return chain.filter(exchange);
                        });
            }

        } catch (Exception ex) {
            return Mono.error(ex);
        }

        return chain.filter(exchange);
    }
}