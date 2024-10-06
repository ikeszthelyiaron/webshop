package hu.progmasters.webshop.security;

import hu.progmasters.webshop.exception.TokenIsInBlackListException;
import hu.progmasters.webshop.repository.BlackListToStoreExpiredTokenRepository;
import hu.progmasters.webshop.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final BlackListToStoreExpiredTokenRepository blackListToStoreExpiredTokenRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String tokenFromRequestHeader = getTokenFromRequestHeader(request);



        if(tokenFromRequestHeader == null || !tokenFromRequestHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;

            //megnézi minden API hívás előtt, hogy a Token a black_list-ben van-e .
            // A ("/noauth) azért kell, hogy az új logint ne ellenőrizze,
            // régi (már blacklist-ben lévő) JWT token még mindig a request fejlécében van, és a rendszer ezt ellenőrzi először(Zita,2024.09.28.)
            //if (!request.getRequestURI().startsWith("/noauth") && !request.getRequestURI().equals("/logout") && blackListToStoreExpiredTokenRepository.existsByToken(jwt))

        }
        if (blackListToStoreExpiredTokenRepository.existsByToken(tokenFromRequestHeader.substring(6)))
        {

            throw new TokenIsInBlackListException();
        }

        try {
            final String jwt = tokenFromRequestHeader.substring(6);
            final String username = jwtService.extractUsername(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (username != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                String tokenFromHeader = getTokenFromRequestHeader(request);
                boolean tokenFromHeaderIsValid = tokenFromRequestHeaderIsValid(tokenFromHeader.substring(6), userDetails);

                if (jwtService.isTokenValid(jwt, userDetails) )
                {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    private boolean tokenFromRequestHeaderIsValid(String token, UserDetails userDetails) {
        return jwtService.isTokenValid(token, userDetails);
    }

    public String getTokenFromRequestHeader(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals("Authorization")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
