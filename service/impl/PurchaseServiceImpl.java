package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.Product;
import hu.progmasters.webshop.domain.Purchase;
import hu.progmasters.webshop.exception.NoProductWithSuchIdException;
import hu.progmasters.webshop.repository.PurchaseRepository;
import hu.progmasters.webshop.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;

    @Override
    public Purchase savePurchase(Purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    public Purchase updatePurchase(Purchase purchase) {
        Optional<Purchase> byId = purchaseRepository.findById(purchase.getId());

        if (byId.isEmpty()) {
            throw new RuntimeException("No purchase with such id");
        }

        return purchaseRepository.save(purchase);
    }
}
