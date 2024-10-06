package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.*;
import hu.progmasters.webshop.dto.ProductInCartDto;
import hu.progmasters.webshop.dto.RegistrationDto;
import hu.progmasters.webshop.dto.UsernameDto;
import hu.progmasters.webshop.dto.mapper.ProductMapper;
import hu.progmasters.webshop.dto.mapper.RegistrationDtoMapper;
import hu.progmasters.webshop.dto.mapper.UserMapper;
import hu.progmasters.webshop.exception.UserNotFoundByIdException;
import hu.progmasters.webshop.exception.UserProfileNotFoundException;
import hu.progmasters.webshop.repository.ConfirmationTokenRepository;
import hu.progmasters.webshop.repository.UserRepository;
import hu.progmasters.webshop.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProductService productService;
    private final PurchaseProductService purchaseProductService;
    private final ProductMapper productMapper;
    private final PurchaseService purchaseService;
    private final UserProfileService userProfileService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final RegistrationDtoMapper registrationDtoMapper;
    private final UserMapper userMapper;
    private final EmailServiceImpl emailService;


    @Override
    public User getAuthenticatedUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("asd");
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Override
    public UsernameDto saveUser(User user) {
//        User user = registrationDtoMapper.dtoToEntity(registrationDto);
        UsernameDto usernameDto = userMapper.entityToUsernameDto(userRepository.save(user));
        handleConfirmationEmail(user);
        userProfileService.saveUseProfile(new UserProfile(user));
        return usernameDto;
    }

    private void handleConfirmationEmail(User user) {
        ConfirmationToken ct = new ConfirmationToken(user);
        log.info("ConfirmationToken generated: " + ct.getConfirmationTokenString());
        confirmationTokenRepository.save(ct);
        sendEmail(user, ct);
    }

    private void sendEmail(User user, ConfirmationToken ct) {
        SimpleMailMessage mailMessage = compileEmail(user, ct);
        emailService.sendEmail(mailMessage);
    }

    private SimpleMailMessage compileEmail(User user, ConfirmationToken token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Please complete your registration");
        mailMessage.setText("To confirm your account, please click here : "

                + "http://localhost:8080/noauth/confirm-account?token=" + token.getConfirmationTokenString());

        log.info("mailMessage generated: " + mailMessage.getTo() + " " +
                mailMessage.getSubject() + " " + mailMessage.getText());
        return mailMessage;
    }


    @Override
    public Double placePurchase(HttpSession session) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ProductInCartDto> myCart = null;
//                productService.getMyCart(session);

        if (myCart.isEmpty()) {
            throw new RuntimeException("No products in cart");
        }

        //  Optional<User> userFromRepo = userRepository.findById(userId);
        Double total = 0.0;

        //  if (userFromRepo.isEmpty()) {
        //      throw new UserNotFoundByIdException(userId);
        //  }

        //  User user = userFromRepo.get();
        UserProfile userProfile = user.getUserProfile();

        if (userProfile == null) {
            throw new UserProfileNotFoundException(user.getId());
        }

        Purchase purchase = new Purchase();
        purchaseService.savePurchase(purchase);


        for (ProductInCartDto dto : myCart) {
            Product product = productService.findProductById(dto.productId());
            PurchaseProduct purchaseProduct = new PurchaseProduct();

            purchaseProduct.setPurchase(purchase);

            purchaseProduct.setProduct(product);
            productService.updateProduct(product);

            purchaseProduct.setPurchasedAmount(dto.amount());

            purchaseProduct.setUserProfile(userProfile);
            userProfileService.saveUseProfile(userProfile);

            purchaseProductService.savePurchaseProduct(purchaseProduct);

            purchase.addPurchaseProductToList(purchaseProduct);
            total += dto.price() * dto.amount();
        }

        purchaseService.updatePurchase(purchase);

//        productService.emptyCart(session);

        return total;
    }


    public void saveSuperAdmin(User user) {
        userRepository.save(user);
        userProfileService.saveUseProfile(new UserProfile(user));
    }
}
