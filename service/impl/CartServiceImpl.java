package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.Cart;
import hu.progmasters.webshop.domain.Product;
import hu.progmasters.webshop.domain.User;
import hu.progmasters.webshop.dto.ProductInCartDto;
import hu.progmasters.webshop.dto.mapper.ProductMapper;
import hu.progmasters.webshop.exception.NoProductWithSuchIdException;
import hu.progmasters.webshop.repository.CartRepository;
import hu.progmasters.webshop.repository.ProductRepository;
import hu.progmasters.webshop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductInCartDto> putProductIntoCart(Long productId) {
        //TODO: SecConfig beáll aut nélk esetre
        //TODO: ha User logs in & Long.MAX id-jú Cart --> hoz rend & id változt
        //TODO: deleteProduct átír
        //TODO: PLACEPURCHASE ÁTÍR
        //TODO: mappelés, returnt írja is ki

        List<ProductInCartDto> result = new ArrayList<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Cart cart;
        if (user == null) {
            cart = handleNullUser();
        } else {
            if (user.getCart() == null) cart = handleNullCart(user);
            else cart = user.getCart();
        }
        ProductInCartDto product = productMapper.entityToProductInCartDto(productRepository.findById(productId).orElseThrow(() ->
                new NoProductWithSuchIdException(productId)), 1);
        if(cart.getProductList().contains(product)) {
            increaseQuantity(productId);
        }
        cart.addProduct(product);
        cartRepository.save(cart);

        return result;
    }

    private Cart handleNullUser() {
        //TODO: J-B elnev
        Cart cart = new Cart();
        cart.setId(Long.MAX_VALUE);
        return cartRepository.findById(Long.MAX_VALUE)
                .orElse(cartRepository.save(cart));
    }

    private Cart handleNullCart(User user) {
        Cart cart = new Cart();
        user.setCart(cart);
        return cartRepository.save(cart);
    }

    @Override
    public List<ProductInCartDto> getMyCart() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ProductInCartDto> result = new ArrayList<ProductInCartDto>();
        if (user == null) {
            Cart cart = cartRepository.findById(Long.MAX_VALUE).orElseThrow(() -> new RuntimeException());
            result = cart.getProductList().stream()
                    .map(p -> productMapper.entityToProductInCartDto(p))
                    .toList();
        }
        return result;
    }

    @Override
    public List<ProductInCartDto> increaseQuantity(Long productId) {
        return List.of();
    }

    @Override
    public List<ProductInCartDto> decreaseQuantity(Long productId) {
        return List.of();
    }

    @Override
    public void emptyMyCart() {
        System.out.println("empty");
    }
}
