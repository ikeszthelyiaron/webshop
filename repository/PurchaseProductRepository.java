package hu.progmasters.webshop.repository;

import hu.progmasters.webshop.domain.PurchaseProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PurchaseProductRepository extends CrudRepository<PurchaseProduct, Long> {

   @Query("SELECT pp from PurchaseProduct pp WHERE pp.userProfile.user.id =:userId")
    List<PurchaseProduct> findPurchasesByUserId(Long userId);
}
