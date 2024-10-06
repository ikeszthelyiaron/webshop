package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.BlackListToStoreExpiredToken;
import hu.progmasters.webshop.repository.BlackListToStoreExpiredTokenRepository;
import hu.progmasters.webshop.security.JwtAuthenticationFilter;
import hu.progmasters.webshop.service.JwtService;
import hu.progmasters.webshop.service.TesztService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TesztServiceImpl implements TesztService {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final BlackListToStoreExpiredTokenRepository   blackListToStoreExpiredTokenRepository;
    private final JwtService jwtService;


    @Override
    public String getString(HttpServletRequest request, HttpServletResponse response) {

        String token = jwtAuthenticationFilter.getTokenFromRequestHeader(request);
        String jwtToken = token;
        if (token != null && token.startsWith("Bearer")) {

            jwtToken = token.substring(6);
        }

        BlackListToStoreExpiredToken blackToken = new BlackListToStoreExpiredToken();
        blackToken.setToken(jwtToken);
        blackToken.setExpirationDate(getExpirationTimeStamp(token));

        blackListToStoreExpiredTokenRepository.save(blackToken);
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setHttpOnly(true);  // HTTP-only to prevent access via JavaScript
        cookie.setSecure(true);    // Secure, use only with HTTPS
        cookie.setPath("/");       // Set path, cookie available throughout the domain
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "Logout is succesful";
        //return jwtToken;
    }
    private Timestamp getExpirationTimeStamp(String token) {
        Long tokenExpInMilliSec = jwtService.getExpirationTime();

        Date tokenIssuedDate = jwtService.extractIssuedAt(token.substring(6));

        Date tokenExpirataionDate = new Date(tokenIssuedDate.getTime() + tokenExpInMilliSec);

        return new Timestamp(tokenExpirataionDate.getTime());

    }
}
