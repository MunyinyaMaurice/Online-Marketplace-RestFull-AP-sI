package com.Awesome.Challenge.Online.Marketplace.API.service;

import com.Awesome.Challenge.Online.Marketplace.API.dto.CategoryDto;
import com.Awesome.Challenge.Online.Marketplace.API.model.Category;
import com.Awesome.Challenge.Online.Marketplace.API.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    public Category createCategory(CategoryDto categoryDto) {

        try {
            Category category = Category.builder()
                    .name(categoryDto.getName())
                    .build();

            return categoryRepository.save(category);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create category. Please try again later.");
        }
    }
    // Service method to update an existing category based on the provided category ID and DTO
    public Category updateCategory(Integer categoryId, CategoryDto categoryDto) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null.");
        }
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + categoryId));
        try {
            existingCategory.setName(categoryDto.getName());

            return categoryRepository.save(existingCategory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update category. Please try again later.");
        }
    }

    // Service method to delete an existing category based on the provided category ID
    public void deleteCategory(Integer categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null.");
        }
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID: " + categoryId));

        try {
            // Delete the existing category from the repository
            categoryRepository.delete(existingCategory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to delete category. Please try again later.");
        }
    }

    // Service method to retrieve a list of all categories
    public List<Category> getAllCategories() {
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve categories. Please try again later.");
        }
    }


}
