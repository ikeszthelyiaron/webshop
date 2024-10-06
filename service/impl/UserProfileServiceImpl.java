package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.PurchaseProduct;
import hu.progmasters.webshop.domain.User;
import hu.progmasters.webshop.domain.UserProfile;
import hu.progmasters.webshop.dto.PurchaseProductResponseDto;
import hu.progmasters.webshop.dto.mapper.PurchaseProductMapper;
import hu.progmasters.webshop.repository.PurchaseProductRepository;
import hu.progmasters.webshop.repository.UserProfileRepository;
import hu.progmasters.webshop.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final PurchaseProductRepository purchaseProductRepository;
    private final PurchaseProductMapper purchaseProductMapper;

    @Override
    public UserProfile saveUseProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    @Override
    public List<PurchaseProductResponseDto> getPurchaseProductsByUserId(Long userId) {
        List<PurchaseProduct> purchaseProductsByUserId = purchaseProductRepository.findPurchasesByUserId(userId);

        return purchaseProductsByUserId.stream()
                .map(purchaseProductMapper::entityToPurchaseProductResponseDto)
                .toList();
    }

    @Override
    public List<PurchaseProductResponseDto> getMyPurchases() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return getPurchaseProductsByUserId(user.getId());

    }

}
