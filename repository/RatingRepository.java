package hu.progmasters.webshop.repository;

import hu.progmasters.webshop.domain.Rating;
import hu.progmasters.webshop.dto.RatingproductAvarageDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface RatingRepository extends CrudRepository<Rating, Long> {

    @Query("SELECT NEW hu.progmasters.webshop.dto.RatingproductAvarageDto(r.product.name,COUNT(r.numberRating), AVG(r.numberRating)) FROM Rating r WHERE r.product.name =:productName ")
    RatingproductAvarageDto getAvarageRatingOdProduct(String productName);
//
}
