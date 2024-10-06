package hu.progmasters.webshop.repository;

import hu.progmasters.webshop.domain.Image;
import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image,Long> {
}
