package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ApplicationException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorCode;
import com.Awesome.Challenge.Online.Marketplace.API.model.Order;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Open End Point for All users")
public class BuyerController {

//    @Autowired
    private final ProductService productService;
    private final ProductRepository productRepository;
    private final OrderService orderService;

   
    // This end point is for any client and it display the list of available product which has quantity > 0
    @Operation(summary = "Listed product.", description = "This end point is for any client and it display the list of available product which has quantity > 0")
    @GetMapping("/listed")
    public ResponseEntity<List<Product>> getListedProducts() {
        List<Product> listedProducts = productService.getListedProducts();
        return ResponseEntity.ok(listedProducts);
    }

    //This end point allow client to search for product from listed
    @Operation(summary = "Search any product.", description = "This end point allow client to search for product by product name")
    @GetMapping("/{searchParam}")
    public ResponseEntity<?> searchProducts(@PathVariable String searchParam) {

            if (searchParam.isEmpty() || searchParam.isBlank()) {
                throw new ApplicationException(ErrorCode.BAD_REQUEST,"try to enter product name");
            }try {
                List<Product> results = productRepository.searchByProductName(searchParam);
                return ResponseEntity.ok(results);

        }catch (ApplicationException e){
           throw  new ApplicationException(ErrorCode.SERVER_ERROR);
        }
    }
    @Operation(summary = "List of ordered product.,",
            description = "This end point allow client to get the list of order their have made (Historical)")
    @GetMapping("/ordered")
    public ResponseEntity<?> orderedProducts() {
        try{
       List <Order> order = orderService.getOrdersForBuyer();
        return ResponseEntity.ok(order);
        } catch (ApplicationException e){
            throw new ApplicationException(ErrorCode.SERVER_ERROR);
        }
    }

}
