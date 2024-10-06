package hu.progmasters.webshop.repository;

import hu.progmasters.webshop.domain.BlackListToStoreExpiredToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;

public interface BlackListToStoreExpiredTokenRepository extends CrudRepository<BlackListToStoreExpiredToken, Long> {

    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM BlackListToStoreExpiredToken b WHERE b.expirationDate < ?1")
    void deleteTokenByExpiredDate(Timestamp now);
}
