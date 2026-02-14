package com.zoo.zoomanagement;

import com.zoo.zoomanagement.model.Enclosure;
import com.zoo.zoomanagement.model.FeedType;
import com.zoo.zoomanagement.model.Species;
import com.zoo.zoomanagement.model.Staff;
import com.zoo.zoomanagement.repository.EnclosureRepository;
import com.zoo.zoomanagement.repository.FeedTypeRepository;
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
    private final FeedTypeRepository feedTypeRepository;

    public DataInitializer(SpeciesRepository speciesRepository,
                           EnclosureRepository enclosureRepository,
                           StaffRepository staffRepository,
                           PasswordEncoder passwordEncoder,
                           FeedTypeRepository feedTypeRepository) {
        this.speciesRepository = speciesRepository;
        this.enclosureRepository = enclosureRepository;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.feedTypeRepository = feedTypeRepository;
    }

    @PostConstruct
    public void initData() {
        // Виды животных
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

        // Пользователи
        if (staffRepository.count() == 0) {
            createStaff("Администратор", "admin", "123456", "ADMIN");
            createStaff("Кассир Иванова", "cashier", "123456", "CASHIER");
            createStaff("Ветеринар Петров", "vet", "123456", "VET");
            createStaff("Смотритель Сидоров", "keeper", "123456", "KEEPER");
        }

        // Виды корма
        if (feedTypeRepository.count() == 0) {
            feedTypeRepository.save(new FeedType(null, "Мясо", "кг", 20.0, "Говядина, курица для хищников"));
            feedTypeRepository.save(new FeedType(null, "Рыба", "кг", 15.0, "Свежая морская рыба"));
            feedTypeRepository.save(new FeedType(null, "Трава", "кг", 50.0, "Свежая трава, сено"));
            feedTypeRepository.save(new FeedType(null, "Овощи", "кг", 30.0, "Морковь, капуста, яблоки"));
            feedTypeRepository.save(new FeedType(null, "Фрукты", "кг", 10.0, "Яблоки, бананы, груши"));
            feedTypeRepository.save(new FeedType(null, "Комбикорм", "кг", 25.0, "Специальный комбикорм"));
        }
    }

    private void createStaff(String name, String login, String password, String role) {
        Staff staff = new Staff();
        staff.setName(name);
        staff.setLogin(login);
        staff.setPassword(passwordEncoder.encode(password));
        staff.setRole(role);
        staffRepository.save(staff);
    }
}
