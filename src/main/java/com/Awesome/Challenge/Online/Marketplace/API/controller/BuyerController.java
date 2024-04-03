package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductRepository;
import com.Awesome.Challenge.Online.Marketplace.API.service.OrderService;
import com.Awesome.Challenge.Online.Marketplace.API.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.*;


@RestController
@RequestMapping("/api/l1/buyer")
@RequiredArgsConstructor
@Tag(name = "Open End Point for All users")
public class BuyerController {

//    @Autowired
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final OrderService orderService;

   
    // This end point is for any client and it display the list of available product which has quantity > 0
    @Operation(summary = "Listed product.", description = "Listed product.")
    @GetMapping("/listed")
    public ResponseEntity<List<Product>> getListedProducts() {
        List<Product> listedProducts = productService.getListedProducts();
        return ResponseEntity.ok(listedProducts);
    }

    //This end point allow client to search for product from listed
    @Operation(summary = "Search by product name")
    @GetMapping("/{searchParam}")
    public List<Product> searchProducts(@PathVariable String searchParam) {

        if (searchParam != null) {
            return productRepository.searchByProductName(searchParam);
        } else {
            return null; // Consider returning an empty list instead of null for better practice
        }
    }
    @Operation(summary = "List of ordered product.")
    @GetMapping("/ordered")
    public ResponseEntity<?> orderedProducts() {

        if (orderService.getOrdersForBuyer().getStatusCode() == HttpStatus.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

        } else {
            ResponseEntity<?> orderedResponseEntity = orderService.getOrdersForBuyer();
            return ResponseEntity.status(orderedResponseEntity.getStatusCode()).body(orderedResponseEntity.getBody());
        }
    }

}
