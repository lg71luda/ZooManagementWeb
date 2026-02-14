package com.zoo.zoomanagement.repository;

import com.zoo.zoomanagement.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    // Найти сотрудника по логину (для аутентификации)
    Optional<Staff> findByLogin(String login);

    // Проверить существование логина (для валидации при регистрации)
    boolean existsByLogin(String login);
}
