package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductDto;
import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductUpdateDto;
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
    @PostMapping("/create_product")
public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDto productDto, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
        response.put("errors", errors);
        response.put("message", "Validation failed");
        return ResponseEntity.badRequest().body(response); // Return a 400 status code for validation errors
    } else {
        // Call ProductService to create product
        ResponseEntity<Map<String,Object>> responseEntity = productService.createProduct(productDto, bindingResult);
        
        return ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody());
    }
}
    @Operation(summary = "Update Product attributes.", description = "Endpoint helps seller and admin to update an existing product.")
    @PutMapping("/{productId}")
    public ResponseEntity<Map<String,Object>> updateProduct(@PathVariable Integer productId, @Valid @RequestBody ProductUpdateDto productDto, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
    
            if (bindingResult.hasErrors()) {
                // Handling validation errors
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                response.put("errors", errors);
                response.put("message", "Validation failed");
                return ResponseEntity.badRequest().body(response); // Return a 400 status code for validation errors
            } else {
                ResponseEntity<Map<String,Object>> updatedProduct = productService.updateProduct(productId, productDto,bindingResult);
            return ResponseEntity.status(updatedProduct.getStatusCode()).body(updatedProduct.getBody());
    }
}
    // Get product and it's images by product ID
    @Operation(summary = "Get product and it's images by product ID.", description = "Endpoint returns product and it's images by product ID.")
    @GetMapping("/images/{productId}")
    public ResponseEntity<?> getProductImageData( @PathVariable Integer productId) {
        try {
            ResponseEntity<?> productWithImageDataDto = productService.getProductWithImageData(productId);
            return ResponseEntity.ok(productWithImageDataDto.getBody());
        } catch (EntityNotFoundException ex) {
            // return ResponseEntity.notFound().body(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }



    // Get product by product ID
    @Operation(summary = "Get product by product ID.", description = "Endpoint to get product info by product ID.")
    @GetMapping("/{productId}")
    
    public ResponseEntity<?> getProductById(@PathVariable Integer productId) {
        Map<String, Object> response = new HashMap<>();
        try {
            ResponseEntity<?> productResponseEntity = productService.findProductById(productId);
            
            response.put("product", productResponseEntity.getBody());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (EntityNotFoundException ex) {
            response.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    @Operation(summary = "Delete product.", description = "Endpoint to delete a product by product ID.")
    @DeleteMapping("/{productId}")
public ResponseEntity<?> deleteProduct(@PathVariable Integer productId) {
    Map<String, Object> response = new HashMap<>();
    try {
        productService.deleteProductById(productId);
        return ResponseEntity.noContent().build(); // Return 204 No Content for successful deletion
    } catch (EntityNotFoundException ex) {
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response); // Return 404 Not Found if product not found
    } catch (Exception ex) {
        response.put("error", "Failed to delete product. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // Return 500 Internal Server Error for other exceptions
    }
}

    // Endpoint to get all Not listed product
    @Operation(summary = "Get all Not listed product.", description = "Endpoint return a list of product which has 0 as qantity.")
    @GetMapping("/notListed")
    public ResponseEntity<List<Product>> getNotListedProducts() {
        List<Product> listedProducts = productService.getNotListedProducts();
        return ResponseEntity.ok(listedProducts);
    }

    // Endpoint to get all listed products sorted by average rating
    @Operation(summary = "Get all listed products sorted by average rating.", description = "Endpoint return a list of products sorted by average rating.")
    @GetMapping("/sortedByRating")
    public ResponseEntity<List<Product>> getProductsSortedByRating() {
        List<Product> products = productService.getProductsSortedByAverageRating();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Endpoint to get all listed products with a minimum average rating
    @Operation(summary = "Display all listed products with a minimum average rating.", description = "Endpoint to get all listed products with a minimum average rating.")
    @GetMapping("/highRated")
    public ResponseEntity<List<Product>> getHighRatedProducts() {
        List<Product> products = productService.getHighRatedProducts(4.0); // Example: Minimum rating of 4.0
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Endpoint to get all listed products sorted by total number of reviews
    @Operation(summary = "Get all listed products sorted by total number of reviews.", description = "Endpoint to get all listed products sorted by total number of reviews.")
    @GetMapping("/popular")
    public ResponseEntity<List<Product>> getPopularProducts() {
        List<Product> products = productService.getPopularProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

}
