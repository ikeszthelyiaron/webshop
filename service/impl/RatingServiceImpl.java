package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.Product;
import hu.progmasters.webshop.domain.PurchaseProduct;
import hu.progmasters.webshop.domain.Rating;
import hu.progmasters.webshop.domain.User;
import hu.progmasters.webshop.dto.RatingRequestDto;
import hu.progmasters.webshop.dto.RatingResponseDto;
import hu.progmasters.webshop.dto.RatingproductAvarageDto;
import hu.progmasters.webshop.dto.mapper.RatingMapper;
import hu.progmasters.webshop.exception.NoProductInUserPurchaseException;
import hu.progmasters.webshop.exception.NoProductWithSuchIdException;
import hu.progmasters.webshop.exception.UserNotExistException;
import hu.progmasters.webshop.repository.ProductRepository;
import hu.progmasters.webshop.repository.RatingRepository;
import hu.progmasters.webshop.repository.UserRepository;
import hu.progmasters.webshop.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;
    private final ProductRepository productRepository;//
    private final UserRepository userRepository;//

    @Override
    public RatingResponseDto createRating(RatingRequestDto ratingRequestDto) {
        User user = userRepository.findByUsername(ratingRequestDto.username()).orElseThrow(() -> new UserNotExistException(ratingRequestDto.username()));
        Product product = productRepository.findById(ratingRequestDto.productId()).orElseThrow(()-> new NoProductWithSuchIdException(ratingRequestDto.productId()));

        if (!ratingRequestDto.username().equals(user.getUsername())) {
            throw new UserNotExistException(ratingRequestDto.username());
        }

        //TODO meg kell nézni, hogy az adott user Purchase-ben benn van-e az adott Product (vásrolt-e már belőle

        List<PurchaseProduct> purchaseProducts = user.getUserProfile().getPurchaseProducts();
        boolean userProfilehasProduct = purchaseProducts.stream()
                        .anyMatch(p -> p.getProduct().getId().equals(product.getId()) );

        if (!userProfilehasProduct) {
            throw new NoProductInUserPurchaseException(ratingRequestDto.productId());
        }

                    Rating rating = ratingMapper.dtoToEntity(ratingRequestDto);

        return  ratingMapper.entityToDto(ratingRepository.save(rating));

    }

    @Override
    public RatingproductAvarageDto getAvarageRatingOdProduct(String productName) {
        return ratingRepository.getAvarageRatingOdProduct(productName);
    }


}
