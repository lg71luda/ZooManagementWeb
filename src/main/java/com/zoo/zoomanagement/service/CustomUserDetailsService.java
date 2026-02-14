package com.zoo.zoomanagement.service;

import com.zoo.zoomanagement.model.Staff;
import com.zoo.zoomanagement.repository.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final StaffRepository staffRepository;

    public CustomUserDetailsService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        log.info("Попытка входа: login='{}'", login);

        Staff staff = staffRepository.findByLogin(login)
                .orElseThrow(() -> {
                    log.error("Пользователь не найден: '{}'", login);
                    return new UsernameNotFoundException("Пользователь не найден: " + login);
                });

        log.info("Пользователь найден: id={}, name={}, role={}",
                staff.getId(), staff.getName(), staff.getRole());

        log.info("Хеш пароля в базе: {}", staff.getPassword());

        String role = staff.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

        UserDetails userDetails = User.builder()
                .username(staff.getLogin())
                .password(staff.getPassword())
                .authorities(Collections.singletonList(authority))
                .build();

        log.info("UserDetails создан: username={}, authorities={}",
                userDetails.getUsername(), userDetails.getAuthorities());

        return userDetails;
    }
}