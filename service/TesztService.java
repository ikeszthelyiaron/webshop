package hu.progmasters.webshop.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface TesztService {
    String getString(HttpServletRequest request, HttpServletResponse response);

}
