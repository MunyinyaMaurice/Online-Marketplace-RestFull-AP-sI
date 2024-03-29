package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductDto;
import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductWithImageDataDto;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import com.Awesome.Challenge.Online.Marketplace.API.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//import static com.sun.org.apache.xalan.internal.xsltc.compiler.sym.error;

@RestController
@RequestMapping("/api/l3/product")
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
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors); // Return a list of error messages
        } else {
            Product product = productService.createProduct(productDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(product); // Return the created product
        }
    }
    // Retrieve all products
    @GetMapping("/All_products")
    public ResponseEntity<List<Product>> getAllProducts(){
        try {
        List<Product> products = productService.getALlProduct();
        return ResponseEntity.ok(products);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve All_products.");
      }
    }

    // Get product and it's images by product ID
    @GetMapping("/image/{productId}")
    public ResponseEntity<?> getProductImageData(@PathVariable Integer productId) {
        try {
            ProductWithImageDataDto productWithImageDataDto = productService.getProductWithImageData(productId);
            return ResponseEntity.ok(productWithImageDataDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve product image data.");
        }
    }
@PutMapping("/update/{productId}")
public ResponseEntity<?> updateProduct(@PathVariable Integer productId, @RequestBody @Valid ProductDto productDto, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("errors", bindingResult.getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList()));
        return ResponseEntity.badRequest().body(response); // Return a JSON object with error messages
    } else {
        Product updatedProduct = productService.updateProduct(productId, productDto);
        return ResponseEntity.ok(updatedProduct);
    }
}



    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer productId) {
        Product product = productService.findProductById(productId);
        return ResponseEntity.ok(product);
    }
    @DeleteMapping("/del/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProductById(productId);
        return ResponseEntity.noContent().build();
    }
    // Endpoint to get all Not listed product
    @GetMapping("/notListed")
    public ResponseEntity<List<Product>> getNotListedProducts() {
        List<Product> listedProducts = productService.getNotListedProducts();
        return ResponseEntity.ok(listedProducts);
    }

    // Endpoint to get all listed products sorted by average rating
    @GetMapping("/sortedByRating")
    public ResponseEntity<List<Product>> getProductsSortedByRating() {
        List<Product> products = productService.getProductsSortedByAverageRating();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Endpoint to get all listed products with a minimum average rating
    @GetMapping("/highRated")
    public ResponseEntity<List<Product>> getHighRatedProducts() {
        List<Product> products = productService.getHighRatedProducts(4.0); // Example: Minimum rating of 4.0
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Endpoint to get all listed products sorted by total number of reviews
    @GetMapping("/popular")
    public ResponseEntity<List<Product>> getPopularProducts() {
        List<Product> products = productService.getPopularProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

}
