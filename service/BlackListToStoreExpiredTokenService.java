package hu.progmasters.webshop.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface BlackListToStoreExpiredTokenService {


    String addTokenToBlacklist(HttpServletRequest request, HttpServletResponse response);
}
