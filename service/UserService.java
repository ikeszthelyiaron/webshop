package hu.progmasters.webshop.service;

import hu.progmasters.webshop.domain.User;
import jakarta.servlet.http.HttpSession;
import hu.progmasters.webshop.dto.RegistrationDto;
import hu.progmasters.webshop.dto.UsernameDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    User getAuthenticatedUser();

    List<User> getAllUsers();

    Double placePurchase(HttpSession session);

    UsernameDto saveUser(User user);

    void saveSuperAdmin(User user);

}
