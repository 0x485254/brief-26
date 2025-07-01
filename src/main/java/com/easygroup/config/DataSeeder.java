package com.easygroup.config;

import com.easygroup.entity.*;
import com.easygroup.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

/**
 * Populate the database with demo data when starting the server.
 */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ListRepository listRepository;
    private final PersonRepository personRepository;
    private final ListShareRepository listShareRepository;
    private final DrawRepository drawRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
            ListRepository listRepository,
            PersonRepository personRepository,
            ListShareRepository listShareRepository,
            DrawRepository drawRepository,
            GroupRepository groupRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.listRepository = listRepository;
        this.personRepository = personRepository;
        this.listShareRepository = listShareRepository;
        this.drawRepository = drawRepository;
        this.groupRepository = groupRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Supprime toutes les données dans le bon ordre (relations dépendantes en
        // dernier)
        // groupRepository.deleteAll();
        // drawRepository.deleteAll();
        // listShareRepository.deleteAll();
        // personRepository.deleteAll();
        // listRepository.deleteAll();
        // userRepository.deleteAll();

        if (userRepository.count() > 0) {
            return;
        }

        Map<Integer, User> users = new HashMap<>();
        for (int i = 1; i <= 20; i++) {
            User user = new User();
            user.setId(fixedUuid(i));
            user.setEmail("user" + i + "@example.com");
            user.setPassword(passwordEncoder.encode("password"));
            user.setFirstName("User" + i);
            user.setLastName("Test" + i);
            user.setIsActivated(true);
            user.setCguDate(LocalDate.now());
            users.put(i, userRepository.save(user));
        }

        Random rnd = new Random();

        for (int i = 1; i <= 20; i++) {
            User owner = users.get(i);
            for (int j = 1; j <= 10; j++) {
                UUID listId = fixedUuid(100 + (i - 1) * 10 + j);
                ListEntity list = new ListEntity();
                list.setId(listId);
                list.setName("List" + j + "_U" + i);
                list.setUser(owner);
                listRepository.save(list);

                List<Person> persons = new ArrayList<>();
                for (int k = 1; k <= 20; k++) {
                    UUID personId = fixedUuid(2000 + (i - 1) * 200 + (j - 1) * 20 + k);
                    Person p = new Person();
                    p.setId(personId);
                    p.setName("P" + k + "_L" + j + "_U" + i);
                    p.setGender(Person.Gender.values()[rnd.nextInt(Person.Gender.values().length)]);
                    p.setAge(18 + rnd.nextInt(20));
                    p.setFrenchLevel(1 + rnd.nextInt(5));
                    p.setOldDwwm(rnd.nextBoolean());
                    p.setTechLevel(1 + rnd.nextInt(5));
                    p.setProfile(Person.Profile.values()[rnd.nextInt(Person.Profile.values().length)]);
                    p.setList(list);
                    personRepository.save(p);
                    persons.add(p);
                }

                // Share list with next user (looping around)
                User shareWith = users.get(i == 20 ? 1 : i + 1);
                ListShare share = new ListShare();
                share.setId(fixedUuid(5000 + (i - 1) * 10 + j));
                share.setList(list);
                share.setSharedWithUser(shareWith);
                listShareRepository.save(share);

                // Create a draw with 4 groups
                Draw draw = new Draw();
                draw.setId(fixedUuid(3000 + (i - 1) * 10 + j));
                draw.setList(list);
                draw.setTitle("Initial draw " + j);
                drawRepository.save(draw);

                List<Group> groups = new ArrayList<>();
                for (int g = 1; g <= 4; g++) {
                    Group group = new Group();
                    group.setId(fixedUuid(4000 + (i - 1) * 40 + (j - 1) * 4 + g));
                    group.setName("G" + g);
                    group.setDraw(draw);
                    group.setPersons(new ArrayList<>());
                    groups.add(group);
                }

                int idx = 0;
                for (Person p : persons) {
                    Group target = groups.get(idx % groups.size());
                    target.getPersons().add(p);
                    idx++;
                }

                groupRepository.saveAll(groups);
            }
        }
    }

    private UUID fixedUuid(int suffix) {
        return UUID.fromString(String.format("00000000-0000-0000-0000-%012d", suffix));
    }
}
