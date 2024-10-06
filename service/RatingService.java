package hu.progmasters.webshop.service;

import hu.progmasters.webshop.dto.RatingRequestDto;
import hu.progmasters.webshop.dto.RatingResponseDto;
import hu.progmasters.webshop.dto.RatingproductAvarageDto;

public interface RatingService {
    RatingResponseDto createRating(RatingRequestDto ratingRequestDto);

    RatingproductAvarageDto getAvarageRatingOdProduct(String productName);


}
