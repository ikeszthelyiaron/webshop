package hu.progmasters.webshop.repository;

import hu.progmasters.webshop.domain.Purchase;
import org.springframework.data.repository.CrudRepository;

public interface PurchaseRepository extends CrudRepository<Purchase, Long> {
}
