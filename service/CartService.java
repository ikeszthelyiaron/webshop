package hu.progmasters.webshop.service;

import hu.progmasters.webshop.dto.ProductInCartDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {

    List<ProductInCartDto> putProductIntoCart(Long productId);

    List<ProductInCartDto> getMyCart();

    List<ProductInCartDto> increaseQuantity(Long productId);

    List<ProductInCartDto> decreaseQuantity(Long productId);

    void emptyMyCart();

}
