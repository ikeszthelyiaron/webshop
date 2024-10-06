package hu.progmasters.webshop.repository;

import hu.progmasters.webshop.domain.UserProfile;
import org.springframework.data.repository.ListCrudRepository;

public interface UserProfileRepository extends ListCrudRepository<UserProfile, Long> {


}
