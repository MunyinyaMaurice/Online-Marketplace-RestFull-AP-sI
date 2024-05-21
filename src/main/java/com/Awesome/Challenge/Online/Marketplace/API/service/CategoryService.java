package com.Awesome.Challenge.Online.Marketplace.API.service;

import com.Awesome.Challenge.Online.Marketplace.API.dto.CategoryDto;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ApplicationException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorCode;
import com.Awesome.Challenge.Online.Marketplace.API.model.Category;
import com.Awesome.Challenge.Online.Marketplace.API.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    public Category createCategory(CategoryDto categoryDto) {


        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ApplicationException(ErrorCode.CONFLICT, "Category name is already taken");
        }
        try {
            Category category = Category.builder()
                    .name(categoryDto.getName())
                    .build();

            return categoryRepository.save(category);
        } catch (ApplicationException e){
            throw new ApplicationException(ErrorCode.SERVER_ERROR,"Failed to create category. Please try again later.");
        }
    }
    // Service method to update an existing category based on the provided category ID and DTO
    public Category updateCategory(Integer categoryId, CategoryDto categoryDto) {
        if (categoryId == null) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST,"Category ID cannot be null.");
        }
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND,"Category not found"));
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ApplicationException(ErrorCode.CONFLICT, "Category name is already taken");
        }
        try {
            existingCategory.setName(categoryDto.getName());

            return categoryRepository.save(existingCategory);
        } catch (ApplicationException e){
            throw new ApplicationException(ErrorCode.SERVER_ERROR,"Failed to update category. Please try again later.");
        }
    }

    // Service method to delete an existing category based on the provided category ID
    public void deleteCategory(Integer categoryId) {
        if (categoryId == null) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST,"Category ID cannot be null.");
        }
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND,"Category not found"));
        try {
            // Delete the existing category from the repository
            categoryRepository.delete(existingCategory);
        } catch (ApplicationException e){
            throw new ApplicationException(ErrorCode.SERVER_ERROR,"Failed to delete category. Please try again later.");
        }
    }

    // Service method to retrieve a list of all categories
    public List<Category> getAllCategories() {
        try {
            return categoryRepository.findAll();
        } catch (ApplicationException e){
            throw new ApplicationException(ErrorCode.SERVER_ERROR,"Failed to retrieve categories. Please try again later.");
        }
    }
}
