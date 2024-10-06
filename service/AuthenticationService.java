package hu.progmasters.webshop.service;

import hu.progmasters.webshop.dto.LoginDto;
import hu.progmasters.webshop.dto.RegistrationDto;
import hu.progmasters.webshop.dto.UserDto;
import hu.progmasters.webshop.dto.UsernameDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthenticationService {

    UsernameDto registerUser(RegistrationDto registrationDto);

    LoginDto login(UserDto userDto, HttpServletResponse response);

    UsernameDto confirmEmail(String confirmationToken);

    UsernameDto registerAdmin(RegistrationDto registrationDto);
}
