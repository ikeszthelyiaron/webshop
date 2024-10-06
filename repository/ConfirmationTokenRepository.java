package hu.progmasters.webshop.repository;

import hu.progmasters.webshop.domain.ConfirmationToken;
import org.springframework.data.repository.ListCrudRepository;


public interface ConfirmationTokenRepository extends ListCrudRepository<ConfirmationToken, Long> {

    ConfirmationToken findByConfirmationTokenString(String confirmationToken);
}
