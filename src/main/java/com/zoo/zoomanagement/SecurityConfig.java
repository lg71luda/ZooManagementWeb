package com.zoo.zoomanagement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/feedstock", "/feedstock/**").hasAnyRole("ADMIN", "KEEPER")
                        .requestMatchers("/veterinary", "/veterinary/**").hasAnyRole("ADMIN", "VET")  // ДОБАВИТЬ
                        .requestMatchers("/staff", "/staff/**").hasRole("ADMIN")
                        .requestMatchers("/tickets", "/tickets/**").hasAnyRole("ADMIN", "CASHIER")
                        .requestMatchers("/feedings", "/feedings/**").hasAnyRole("ADMIN", "KEEPER")
                        .requestMatchers("/animals", "/animals/**")
                        .hasAnyRole("ADMIN", "VET", "KEEPER", "CASHIER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}