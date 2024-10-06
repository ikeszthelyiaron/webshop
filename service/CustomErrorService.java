package hu.progmasters.webshop.service;

import jakarta.servlet.http.HttpServletRequest;

public interface CustomErrorService {

    String handleError(HttpServletRequest request);

    String cancelPayment(String token);
}
