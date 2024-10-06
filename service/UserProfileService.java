package hu.progmasters.webshop.service;

import hu.progmasters.webshop.domain.UserProfile;
import hu.progmasters.webshop.dto.PurchaseProductResponseDto;

import java.util.List;

public interface UserProfileService {
    UserProfile saveUseProfile(UserProfile userProfile);

    List<PurchaseProductResponseDto> getPurchaseProductsByUserId(Long userId);

    List<PurchaseProductResponseDto> getMyPurchases();

}
