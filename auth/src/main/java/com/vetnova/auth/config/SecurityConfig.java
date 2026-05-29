package com.vetnova.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Este objeto se encargará de encriptar las claves de forma segura
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Se deshabilita CSRF porque usaremos JWT
            .authorizeHttpRequests(auth -> auth
                // Permitimos que cualquiera se registre o inicie sesión
                .requestMatchers("/api/auth/**").permitAll() 
                
                .requestMatchers("/api/veterinaria/historial/**").hasAnyRole("VETERINARIO", "ADMINISTRADOR")
                .requestMatchers("/api/veterinaria/citas/**").hasAnyRole("RECEPCIONISTA", "ADMINISTRADOR", "CLIENTE")
                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                
                .anyRequest().authenticated()
            );
            
        return http.build();
    }
}
