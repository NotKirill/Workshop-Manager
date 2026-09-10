package notkirill.workshopmanager.security;

import notkirill.workshopmanager.exception.ErrorService;
import notkirill.workshopmanager.security.auth.jwt.JwtAuthFilter;
import notkirill.workshopmanager.security.auth.jwt.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityFilterChainConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityFilterChainConfig.class);

    private final JwtAuthFilter jwtAuthFilter;
    private final ErrorService errorService;

    @Autowired
    public SecurityFilterChainConfig(JwtService jwtService, UserDetailsService userDetailsService,
                                     ErrorService errorService) {
        this.jwtAuthFilter = new JwtAuthFilter(jwtService, userDetailsService);
        this.errorService = errorService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .cors(customizer -> {
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(customizer -> customizer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(customizer -> customizer
                        .requestMatchers("/api/public/**", "/api/auth/**").permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                                          errorService.writeError(
                                                                  log, authException, HttpStatus.UNAUTHORIZED,
                                                                  "UNAUTHORIZED",
                                                                  request, response
                                                          ))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                                     errorService.writeError(
                                                             log, accessDeniedException, HttpStatus.FORBIDDEN,
                                                             "FORBIDDEN",
                                                             request, response
                                                     ))
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
