package hu.progmasters.webshop.util;

import hu.progmasters.webshop.domain.Role;
import hu.progmasters.webshop.domain.User;
import hu.progmasters.webshop.repository.UserRepository;
import hu.progmasters.webshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuperAdminSeeder {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ContextRefreshedEvent.class)
    public void seedSuperAdmin() {
        if(!superAdminExists()) {
            User user = createSuperAdmin();
            userService.saveSuperAdmin(user);
        }
    }

    private User createSuperAdmin() {
        User user = new User();
        user.setUsername("superadmin");
        user.setPassword(passwordEncoder.encode("test1234"));
        user.setRole(Role.SUPER_ADMIN);
        user.setEnabled(true);
        return user;
    }

    private boolean superAdminExists() {
        return userRepository.existsByRole(Role.SUPER_ADMIN);
    }
}
