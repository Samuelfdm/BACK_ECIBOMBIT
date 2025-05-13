package edu.eci.arsw.ecibombit.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

import lombok.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtValidator jwtValidator;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/users/login", "/users/register").permitAll()
                .requestMatchers("/users/**").authenticated()
                .requestMatchers("/games/create").authenticated()
                .requestMatchers("/games/**").authenticated()
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtValidator, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean(name = "corsConfigurerSecurity")
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 
                        .allowedOrigins("http://localhost:3000")
                        .allowedOrigins("http://localhost:5173") 
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") 
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("https://login.microsoftonline.com/9dc4175a-6862-48a3-b836-f693f6327e6b/v2.0");
    }
}