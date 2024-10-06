package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
//    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }
}