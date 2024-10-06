package hu.progmasters.webshop.repository;

import hu.progmasters.webshop.domain.Category;
import hu.progmasters.webshop.dto.CategoryResponseDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    @Query ("SELECT NEW hu.progmasters.webshop.dto.CategoryResponseDto(c.categoryName,c.id) FROM Category c")
    List<CategoryResponseDto> getAllCategories();

    Optional<Category> findByCategoryName(String categoryName);
}
