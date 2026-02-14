package com.zoo.zoomanagement;

import com.zoo.zoomanagement.model.Enclosure;
import com.zoo.zoomanagement.model.Species;
import com.zoo.zoomanagement.model.Staff;
import com.zoo.zoomanagement.repository.EnclosureRepository;
import com.zoo.zoomanagement.repository.SpeciesRepository;
import com.zoo.zoomanagement.repository.StaffRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final SpeciesRepository speciesRepository;
    private final EnclosureRepository enclosureRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(SpeciesRepository speciesRepository,
                           EnclosureRepository enclosureRepository,
                           StaffRepository staffRepository,
                           PasswordEncoder passwordEncoder) {
        this.speciesRepository = speciesRepository;
        this.enclosureRepository = enclosureRepository;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initData() {
        // Виды
        if (speciesRepository.count() == 0) {
            speciesRepository.save(new Species(null, "Лев", "Саванна", "Хищник"));
            speciesRepository.save(new Species(null, "Жираф", "Саванна", "Травоядное"));
            speciesRepository.save(new Species(null, "Пингвин", "Антарктида", "Рыба"));
            speciesRepository.save(new Species(null, "Слон", "Саванна", "Травоядное"));
            speciesRepository.save(new Species(null, "Тигр", "Тайга", "Хищник"));
        }

        // Вольеры
        if (enclosureRepository.count() == 0) {
            enclosureRepository.save(new Enclosure(null, "Вольер А1 (хищники)", 4, 0));
            enclosureRepository.save(new Enclosure(null, "Вольер А2 (травоядные)", 6, 0));
            enclosureRepository.save(new Enclosure(null, "Бассейн пингвинов", 20, 0));
            enclosureRepository.save(new Enclosure(null, "Саванна большая", 10, 0));
            enclosureRepository.save(new Enclosure(null, "Террариум", 15, 0));
        }

        // Пользователи - СОЗДАЁМ НЕСКОЛЬКО ДЛЯ ТЕСТОВ
        if (staffRepository.count() == 0) {
            createStaff("Администратор", "admin", "123456", "ADMIN");
            createStaff("Кассир Иванова", "cashier", "123456", "CASHIER");
            createStaff("Ветеринар Петров", "vet", "123456", "VET");
            createStaff("Смотритель Сидоров", "keeper", "123456", "KEEPER");
        }
    }

    private void createStaff(String name, String login, String password, String role) {
        Staff staff = new Staff();
        staff.setName(name);
        staff.setLogin(login);
        staff.setPassword(passwordEncoder.encode(password)); // Хешируем пароль!
        staff.setRole(role);
        staffRepository.save(staff);
    }
}
