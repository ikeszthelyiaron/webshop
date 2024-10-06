package hu.progmasters.webshop.repository;

import hu.progmasters.webshop.domain.Cart;
import org.springframework.data.repository.ListCrudRepository;

public interface CartRepository extends ListCrudRepository<Cart, Long> {
}
