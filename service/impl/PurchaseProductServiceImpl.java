package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.PurchaseProduct;
import hu.progmasters.webshop.repository.PurchaseProductRepository;
import hu.progmasters.webshop.service.PurchaseProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseProductServiceImpl implements PurchaseProductService {

    private final PurchaseProductRepository purchaseProductRepository;

    public void savePurchaseProduct(PurchaseProduct purchaseProduct) {
        purchaseProductRepository.save(purchaseProduct);
    }
}
