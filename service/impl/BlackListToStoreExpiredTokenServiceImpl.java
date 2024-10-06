package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.BlackListToStoreExpiredToken;
import hu.progmasters.webshop.repository.BlackListToStoreExpiredTokenRepository;
import hu.progmasters.webshop.security.JwtAuthenticationFilter;
import hu.progmasters.webshop.service.BlackListToStoreExpiredTokenService;
import hu.progmasters.webshop.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlackListToStoreExpiredTokenServiceImpl implements BlackListToStoreExpiredTokenService {

    private final BlackListToStoreExpiredTokenRepository blackListToStoreExpiredTokenRepository;
    private final JwtService jwtService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Override
    public String addTokenToBlacklist(HttpServletRequest request, HttpServletResponse response) {

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

    }

    private Timestamp getExpirationTimeStamp(String token) {
        Long tokenExpInMilliSec = jwtService.getExpirationTime();

        Date tokenIssuedDate = jwtService.extractIssuedAt(token.substring(6));

        Date tokenExpirataionDate = new Date(tokenIssuedDate.getTime() + tokenExpInMilliSec);

        return new Timestamp(tokenExpirataionDate.getTime());

    }

    @Scheduled(cron = "${purge.cron.expression}")
    public void deleteExpiredToken() {

        Timestamp now = new Timestamp(System.currentTimeMillis());
        blackListToStoreExpiredTokenRepository.deleteTokenByExpiredDate(now);

    }

}
