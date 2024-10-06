package hu.progmasters.webshop.service;

import hu.progmasters.webshop.domain.CompletedPurchase;
import hu.progmasters.webshop.domain.PaymentPurchase;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;

public interface PaypalService {
    PaymentPurchase createPayment(BigDecimal sum);

    CompletedPurchase completePayment(String token);

}
