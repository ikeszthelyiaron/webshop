package hu.progmasters.webshop.service.impl;

import hu.progmasters.webshop.domain.Category;
import hu.progmasters.webshop.dto.CategoryRequestDto;
import hu.progmasters.webshop.dto.CategoryResponseDto;
import hu.progmasters.webshop.dto.mapper.CategoryMapper;
import hu.progmasters.webshop.exception.CategoryAlreadyExistException;
import hu.progmasters.webshop.exception.CategoryNotFoundByIdExceptions;
import hu.progmasters.webshop.repository.CategoryRepository;
import hu.progmasters.webshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto requestDto) {
        categoryRepository.findByCategoryName(requestDto.name()).ifPresent(category -> {
            throw new CategoryAlreadyExistException(requestDto.name());});

        return categoryMapper.entityToDto(categoryRepository.save(categoryMapper.dtoToEntity(requestDto)));
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundByIdExceptions(id));
        categoryRepository.delete(category);
    }
}
