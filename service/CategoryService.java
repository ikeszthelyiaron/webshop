package hu.progmasters.webshop.service;

import hu.progmasters.webshop.dto.CategoryRequestDto;
import hu.progmasters.webshop.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto requestDto);

    List<CategoryResponseDto> getAllCategories();

    void deleteCategory(Long id);
}
