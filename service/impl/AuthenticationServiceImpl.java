package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.ConfirmationToken;
import hu.progmasters.webshop.domain.Role;
import hu.progmasters.webshop.domain.User;
import hu.progmasters.webshop.dto.LoginDto;
import hu.progmasters.webshop.dto.RegistrationDto;
import hu.progmasters.webshop.dto.UserDto;
import hu.progmasters.webshop.dto.UsernameDto;
import hu.progmasters.webshop.dto.mapper.RegistrationDtoMapper;
import hu.progmasters.webshop.dto.mapper.UserMapper;
import hu.progmasters.webshop.exception.EmailAlreadyInUseException;
import hu.progmasters.webshop.exception.IncorrectCredentialsException;
import hu.progmasters.webshop.exception.InvalidConfirmationTokenException;
import hu.progmasters.webshop.exception.UsernameAlreadyInUseException;
import hu.progmasters.webshop.repository.ConfirmationTokenRepository;
import hu.progmasters.webshop.repository.UserRepository;
import hu.progmasters.webshop.service.AuthenticationService;
import hu.progmasters.webshop.service.JwtService;
import hu.progmasters.webshop.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserMapper userMapper;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final RegistrationDtoMapper registrationDtoMapper;

    @Override
    public UsernameDto registerUser(RegistrationDto registrationDto) {
        User user = registrationDtoMapper.dtoToEntity(registrationDto);

        return userCanBeSaved(user) ? userService.saveUser(user) : null;
    }



    @Override
    public LoginDto login(UserDto userDto, HttpServletResponse response) {

        authenticate(userDto);

        User user = userRepository.findByUsername(userDto.username())
                .orElseThrow(() -> new IncorrectCredentialsException("Wrong username or password. Please try again."));

        String jwtToken = jwtService.generateToken(user);

        Cookie cookie = createCookie(jwtToken);

        LoginDto loginDto = new LoginDto(jwtToken, jwtService.getExpirationTime());

        response.addCookie(cookie);

        return loginDto;
    }

    private void authenticate(UserDto userDto) {
        daoAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.username(),
                        userDto.password()
                )
        );
    }

    private Cookie createCookie(String jwtToken) {
        Cookie cookie = new Cookie("Authorization", "Bearer" + jwtToken);
        cookie.setHttpOnly(true);  // HTTP-only to prevent access via JavaScript
        cookie.setSecure(true);    // Secure, use only with HTTPS
        cookie.setPath("/");       // Set path, cookie available throughout the domain
        cookie.setMaxAge(60 * 60 * 60);
        return cookie;
    }


    @Override
    public UsernameDto confirmEmail(String token) {
        ConfirmationToken confirmationToken =
                confirmationTokenRepository.findByConfirmationTokenString(token);

        if(confirmationToken == null) {
            throw new InvalidConfirmationTokenException(token);
        }

        User user = userRepository.findByEmailIgnoreCase(confirmationToken.getUser().getEmail())
                .orElseThrow(() -> new RuntimeException("user not found..."));
        user.setEnabled(true);
        return userMapper.entityToUsernameDto(userRepository.save(user));
    }


    @Override
    public UsernameDto registerAdmin(RegistrationDto registrationDto) {
        User user = registrationDtoMapper.dtoToEntity(registrationDto);
        user.setRole(Role.ADMIN);
        return userCanBeSaved(user) ? userService.saveUser(user) : null;
    }

    private boolean userCanBeSaved(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyInUseException(user.getEmail());
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyInUseException(user.getUsername());
        }
        return true;
    }

}
