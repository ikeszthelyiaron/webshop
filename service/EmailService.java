package hu.progmasters.webshop.service;


import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;

public interface EmailService {

//    @Async
    void sendEmail(SimpleMailMessage email);


}
