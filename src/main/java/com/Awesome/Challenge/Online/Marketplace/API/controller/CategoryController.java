package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.dto.CategoryDto;
import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductDto;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ApplicationException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorResponse;
import com.Awesome.Challenge.Online.Marketplace.API.handleValidation.HandleValidationErrors;
import com.Awesome.Challenge.Online.Marketplace.API.model.Category;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import com.Awesome.Challenge.Online.Marketplace.API.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Tag(name = "Category Management")

public class CategoryController {

    private final CategoryService categoryService;
    private final HandleValidationErrors handleValidationErrors;

    // This end point helps to handle HTTP POST requests for creating a new category
    @PostMapping("/createCategory")
    @Operation(summary = "Create a new category.", description = "Endpoint to create a new category.")
    public ResponseEntity<?> createCategories(@RequestBody @Valid CategoryDto categoryDto,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationErrors.handleValidationErrors(bindingResult);
        }
        try {
            Category category = categoryService.createCategory(categoryDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(category);
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }
    // Endpoint to update an existing category
    @Operation(summary = "Update category.", description = "Endpoint to update an existing category.")
    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer categoryId, @Valid @RequestBody CategoryDto categoryDto,
                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationErrors.handleValidationErrors(bindingResult);
        }
        try {
            Category updatedCategory = categoryService.updateCategory(categoryId, categoryDto);
            return ResponseEntity.ok(updatedCategory);
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }

    // Endpoint to delete an existing category
    @Operation(summary = "Delete category.", description = "Endpoint to delete an existing category")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return ResponseEntity.noContent().build();
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }

    // Endpoint to list all categories
    @Operation(summary = "List of all category.", description = "Endpoint to list all categories.")
    @GetMapping("/all_list")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }
}
