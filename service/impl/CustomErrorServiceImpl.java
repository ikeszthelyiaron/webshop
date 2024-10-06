package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.service.CustomErrorService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CustomErrorServiceImpl implements CustomErrorService {
    @Override
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error-404";
            }
        }
        return "error";
    }

    @Override
    public String cancelPayment(String token) {
        return "cancel";
    }
}
