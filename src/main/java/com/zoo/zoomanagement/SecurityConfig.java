package com.zoo.zoomanagement;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/staff", "/staff/**").hasRole("ADMIN")
                        .requestMatchers("/tickets", "/tickets/**").hasAnyRole("ADMIN", "CASHIER")
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
    public UserDetailsService userDetailsService() {
        var encoder = new BCryptPasswordEncoder();
        var admin = User.withUsername("admin")
                .password(encoder.encode("123"))
                .roles("ADMIN")
                .build();
        var cashier = User.withUsername("cashier")
                .password(encoder.encode("123"))
                .roles("CASHIER")
                .build();
        var vet = User.withUsername("vet")
                .password(encoder.encode("123"))
                .roles("VET")
                .build();
        var keeper = User.withUsername("keeper")
                .password(encoder.encode("123"))
                .roles("KEEPER")
                .build();

        return new InMemoryUserDetailsManager(admin, cashier, vet, keeper);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}