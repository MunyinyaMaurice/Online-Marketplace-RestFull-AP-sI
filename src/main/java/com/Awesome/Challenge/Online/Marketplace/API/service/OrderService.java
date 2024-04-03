package com.Awesome.Challenge.Online.Marketplace.API.service;

import com.Awesome.Challenge.Online.Marketplace.API.dto.OrderDto;
import com.Awesome.Challenge.Online.Marketplace.API.exception.UnauthorizedAccessException;
import com.Awesome.Challenge.Online.Marketplace.API.model.*;
import com.Awesome.Challenge.Online.Marketplace.API.repository.OrderRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.Awesome.Challenge.Online.Marketplace.API.secuirity.config.ApplicationConfig.getCurrentUser;
import static com.Awesome.Challenge.Online.Marketplace.API.secuirity.config.ApplicationConfig.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;
    private final ProductService productService;

    // This method implement the logic of submintting product order for current logged-in user
    public ResponseEntity<?> placeOrder( Integer productId,@Valid OrderDto orderDto, BindingResult bindingResult){
            Map<String, Object> response = new HashMap<>();
            if (bindingResult.hasErrors()) {
                // Handling validation errors
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
                response.put("errors", errors);
                response.put("message", "Validation failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }try {
        Integer buyerId = getCurrentUserId();

        // Check if the User with the provided Id exists
        User user = userRepository.findById(buyerId)
                .orElseThrow(() -> new EntityNotFoundException("Try to log in before order"));
        // Check if the Product with the provided Id exists
        Product product  = productRepository.findById(productId)
                .orElseThrow(()->new EntityNotFoundException("Product not found "));

        // Check if the chosen quantity exceeds the available quantity
        if (orderDto.getQuantity() > product.getQuantity()) {
            // return ResponseEntity.badRequest().body("Desired quantity exceeds available quantity for product with ID: " + productId);
            response.put("message", "Desired quantity exceeds available quantity for product with ID: " + productId);
            // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            // throw new EntityNotFoundException("Desired quantity exceeds available quantity for product with ID: " + productId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Calculate the total price
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(orderDto.getQuantity()));

        // Reduce the purchased from available quantity of the product & update Db
        int newQuantity = product.getQuantity() - orderDto.getQuantity();
        product.setQuantity(newQuantity);

        // If the new quantity is zero, set the product as not listed
        if (newQuantity == 0) {
            product.setListed(false);
        }

        productRepository.save(product);

        // Create and save order
        Order order = Order.builder()
                .buyer(user)
                .product(product)
                .quantity(orderDto.getQuantity())
                .totalPrice(totalPrice)
                .orderStatus(OrderStatus.PENDING)
                .build();
                orderRepository.save(order);
                return ResponseEntity.ok(order);
            } catch (EntityNotFoundException ex) {
                response.put("message", ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
             }catch (Exception ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);    
            }

    }
    //This method checks the user's role and ensures that only authorized users can update orders.
    // public ResponseEntity<?> updateOrderStatus(Integer orderId, OrderStatus newStatus, BindingResult bindingResult) {

    public ResponseEntity<?> updateOrderStatus(Integer orderId, Map<String, String> requestBody, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        
        if (bindingResult.hasErrors()) {
            // Handling validation errors
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            response.put("errors", errors);
            response.put("message", "Validation failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } try {
    
            User loggedInUser = getCurrentUser();
    
            if (loggedInUser == null) {
                throw new UnauthorizedAccessException("User is not logged in.");
            }

            // Get the order status from the request body
            String orderStatus = requestBody.get("orderStatus");
    
            // Check if the orderStatus is null or empty
            if (orderStatus == null || orderStatus.isEmpty()) {
                return ResponseEntity.badRequest().body("Order status is required.");
            }
    
            // Convert the orderStatus string to OrderStatus enum
            OrderStatus newStatus = OrderStatus.valueOf(orderStatus);

            // Check if the user is authorized to update orders
    
            if (loggedInUser.getRole().equals(Role.SELLER)) {
                List<Order> sellerOrders = getOrdersForLoggedInSeller();
    
                Optional<Order> orderToUpdate = sellerOrders.stream()
                        .filter(order -> order.getId().equals(orderId))
                        .findFirst();

    
                if (orderToUpdate.isPresent()) {
                    Order order = orderToUpdate.get();
                    order.setOrderStatus(newStatus);
                    return ResponseEntity.ok(orderRepository.save(order));
                } else {
                    throw new UnauthorizedAccessException("User is not authorized to update this order.");
                }
            } else if (loggedInUser.getRole().equals(Role.ADMIN)) {
                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
    
                order.setOrderStatus(newStatus);
                return ResponseEntity.ok(orderRepository.save(order));
            } else {
                throw new UnauthorizedAccessException("User is not authorized to update orders.");
            }
        } catch (EntityNotFoundException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (UnauthorizedAccessException ex) {
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (IllegalArgumentException ex) {
            response.put("message", "Invalid order status.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception ex) {
            response.put("message", "An error occurred while updating the order status.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    


    //This method check if user with SELLER role has any order on product associeted to them if user is ADMIN orders are listed
    public List<Order> getOrdersForLoggedInSeller() {
        User loggedInUser = getCurrentUser();
        if (loggedInUser == null) {
            throw new UnauthorizedAccessException("User not logged in");
        }

        if (loggedInUser.getRole().equals(Role.SELLER)) {
            List<Product> sellerProducts = productService.getProductsBySeller(loggedInUser.getId());
            return orderRepository.findByProductIn(sellerProducts);
        } else if (loggedInUser.getRole().equals(Role.ADMIN)) {
            return orderRepository.findAll();
        } else {
            throw new UnauthorizedAccessException("User is not authorized to access this resource");
        }
    }
    // public ResponseEntity<?> getOrdersForBuyer(BindingResult bindingResult) {
        public ResponseEntity<?> getOrdersForBuyer() {
        Map<String, Object> response = new HashMap<>();
        
        // if (bindingResult.hasErrors()) {
            
        //     response.put("message", "Validation failed");
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        // } 
        try {
        User loggedInUser = getCurrentUser();
        

        if (loggedInUser== null) {
            throw new UnauthorizedAccessException("User is not logged in.");
        } else  {
            List<Order> orderedProducts = orderRepository.getOrderForBuyer(loggedInUser.getId());
            response.put("ordered", orderedProducts);
            return ResponseEntity.ok(orderedProducts);
        }
    } catch (UnauthorizedAccessException ex) {
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }catch (Exception ex) {
        response.put("message", "An error occurred while updating the order status.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
}