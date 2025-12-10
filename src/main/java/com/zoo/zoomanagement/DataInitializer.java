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

        // Админ
        if (staffRepository.count() == 0) {
            Staff admin = new Staff();
            admin.setName("Администратор");
            admin.setLogin("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole("ADMIN");
            staffRepository.save(admin);
        }
    }
}