package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductDto;
import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductUpdateDto;
import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductWithImageDataDto;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ApplicationException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorResponse;
import com.Awesome.Challenge.Online.Marketplace.API.handleValidation.HandleValidationErrors;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import com.Awesome.Challenge.Online.Marketplace.API.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.error;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@Tag(name = "Product management")

public class ProductController {

    private final ProductService productService;
    private final HandleValidationErrors handleValidationErrors;

    @Operation(
            description = "Endpoint to create a new product.",
            summary = "Create a new product.",
            responses = {
                    @ApiResponse(
                            description = "Product created successfully.",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    // Create a new product
    @PostMapping
public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDto productDto,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationErrors.handleValidationErrors(bindingResult);
        }
        try {
        // Call ProductService to create product
        Product product = productService.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
}
    @Operation(summary = "Update Product attributes.",
            description = "Endpoint helps seller and admin to update an existing product.")

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer productId,
                                                            @Valid @RequestBody ProductUpdateDto productDto,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationErrors.handleValidationErrors(bindingResult);
        }try {
                Product updatedProduct = productService.updateProduct(productId, productDto);
            return ResponseEntity.ok(updatedProduct);
    }catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
}
    // Get product and it's images by product ID
    @Operation(summary = "Get product and it's images by product ID.",
            description = "Endpoint returns product and it's images by product ID.")

    @GetMapping("/images/{productId}")
    public ResponseEntity<?> getProductImageData( @PathVariable Integer productId) {
        try {
            ProductWithImageDataDto productWithImageDataDto = productService.getProductWithImageData(productId);
            return ResponseEntity.ok(productWithImageDataDto);
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }



    // Get product by product ID
    @Operation(summary = "Get product by product ID.", description = "Endpoint to get product info by product ID.")
    @GetMapping("/{productId}")
    
    public ResponseEntity<?> getProductById(@PathVariable Integer productId) {
        try {
            Product productResponseEntity = productService.findProductById(productId);
            return ResponseEntity.ok(productResponseEntity);
        } catch (ApplicationException e) {
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(), e.getMessage()));
        }
    }
    @Operation(summary = "Delete product.", description = "Endpoint to delete a product by product ID.")
    @DeleteMapping("/{productId}")
public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {
    try {
        productService.deleteProductById(productId);
        return ResponseEntity.noContent().build(); // Return 204 No Content for successful deletion
    } catch (ApplicationException ex) {
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
                .body(new ErrorResponse(ex.getErrorCode(), ex.getMessage()));
    }
}

    // Endpoint to get all Not listed product
    @Operation(summary = "Get all Not listed product.", description = "Endpoint return a list of product which has 0 as qantity.")
    @GetMapping("/notListed")
    public ResponseEntity<?> getNotListedProducts() {
        try {
            List<Product> listedProducts = productService.getNotListedProducts();
            return ResponseEntity.ok(listedProducts);
        }catch (ApplicationException ex){
            return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(ex.getErrorCode(),ex.getMessage()));
        }

    }

    // Endpoint to get all listed products sorted by average rating
    @Operation(summary = "Get all listed products sorted by average rating.", description = "Endpoint return a list of products sorted by average rating.")
    @GetMapping("/sortedByRating")
    public ResponseEntity<?> getProductsSortedByRating() {
        try {
            List<Product> products = productService.getProductsSortedByAverageRating();
            return ResponseEntity.ok(products);
        }catch (ApplicationException e){
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(),e.getMessage()));
        }
    }

    // Endpoint to get all listed products with a minimum average rating
    @Operation(summary = "Display all listed products with a minimum average rating.", description = "Endpoint to get all listed products with a minimum average rating.")
    @GetMapping("/highRated")
    public ResponseEntity<?> getHighRatedProducts() {
        try {
            List<Product> products = productService.getHighRatedProducts(4.0); // Example: Minimum rating of 4.0
            return ResponseEntity.ok(products);
        }catch (ApplicationException e){
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(),e.getMessage()));
        }
    }

    // Endpoint to get all listed products sorted by total number of reviews
    @Operation(summary = "Get all listed products sorted by total number of reviews.", description = "Endpoint to get all listed products sorted by total number of reviews.")
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularProducts() {
        try{
        List<Product> products = productService.getPopularProducts();
        return ResponseEntity.ok(products);
        }catch (ApplicationException e){
            return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                    .body(new ErrorResponse(e.getErrorCode(),e.getMessage()));
        }
    }

}
