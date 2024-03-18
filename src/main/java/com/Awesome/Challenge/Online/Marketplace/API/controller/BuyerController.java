package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductRepository;
import com.Awesome.Challenge.Online.Marketplace.API.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/l1")
@RequiredArgsConstructor
@Tag(name = "Open End Point for anyone")
public class BuyerController {

//    @Autowired
    private final ProductService productService;
    private final ProductRepository productRepository;

    @Operation(
            description = "Listed product.",
            summary = "This end point is for any client and it display the list of available product which has quantity " +
                    "greater than zero"

    )
    // This end point is for any client and it display the list of available product which has quantity > 0
    @GetMapping("/listed")
    public ResponseEntity<List<Product>> getListedProducts() {
        List<Product> listedProducts = productService.getListedProducts();
        return ResponseEntity.ok(listedProducts);
    }

    //This end point allow client to search for product from listed
    @GetMapping("/search/{searchParam}")
    public List<Product> searchProducts(@PathVariable String searchParam) {

        if (searchParam != null) {
            return productRepository.searchByProductName(searchParam);
        } else {
            return null; // Consider returning an empty list instead of null for better practice
        }
    }

}
