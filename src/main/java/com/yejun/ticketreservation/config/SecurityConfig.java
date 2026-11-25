package com.yejun.ticketreservation.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.core.*;

import com.yejun.ticketreservation.repository.UserRepository;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                        // Public APIs
                        .requestMatchers(HttpMethod.GET, "/events", "/events/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/refresh", "/auth/logout").permitAll()

                        // Event management
                        .requestMatchers("/events", "/events/**").hasRole("ADMIN")

                        // User management
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/*").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/*").hasAnyRole("USER", "ADMIN")

                        // Reservations
                        .requestMatchers("/reservations", "/reservations/**").hasAnyRole("USER", "ADMIN")

                        .anyRequest().denyAll()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter converter =
                new JwtGrantedAuthoritiesConverter();

        converter.setAuthoritiesClaimName("role");
        converter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter authenticationConverter =
                new JwtAuthenticationConverter();

        authenticationConverter
                .setJwtGrantedAuthoritiesConverter(converter);

        return authenticationConverter;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(provider);
    }

    @Bean
    public SecretKey jwtSecretKey(
            @Value("${app.jwt.secret}") String secret) {

        byte[] keyBytes = Base64.getDecoder().decode(secret);

        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {

        return NimbusJwtEncoder.withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(
            SecretKey secretKey,
            @Value("${app.jwt.issuer}") String issuer,
            UserRepository userRepository
    ) {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder.withSecretKey(secretKey)
                        .macAlgorithm(MacAlgorithm.HS256)
                        .build();

        OAuth2TokenValidator<Jwt> defaultValidator =
                JwtValidators.createDefaultWithIssuer(issuer);

        OAuth2TokenValidator<Jwt> activeUserValidator = jwt -> {

            Long userId;

            try {
                userId = Long.valueOf(jwt.getSubject());
            } catch (NumberFormatException e) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token"));
            }

            if (!userRepository.existsByIdAndActiveTrue(userId)) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token"));
            }

            return OAuth2TokenValidatorResult.success();
        };

        decoder.setJwtValidator(
                new DelegatingOAuth2TokenValidator<>(
                        defaultValidator,
                        activeUserValidator
                )
        );

        return decoder;
    }
}