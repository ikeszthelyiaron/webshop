package hu.progmasters.webshop.service;

import hu.progmasters.webshop.domain.Purchase;

public interface PurchaseService {

    Purchase savePurchase(Purchase purchase);

    Purchase updatePurchase(Purchase purchase);
}
