package com.Awesome.Challenge.Online.Marketplace.API.service;

import com.Awesome.Challenge.Online.Marketplace.API.dto.OrderDto;
import com.Awesome.Challenge.Online.Marketplace.API.exception.UnauthorizedAccessException;
import com.Awesome.Challenge.Online.Marketplace.API.model.*;
import com.Awesome.Challenge.Online.Marketplace.API.repository.OrderRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.UserRepository;
import com.Awesome.Challenge.Online.Marketplace.API.secuirity.config.ApplicationConfig;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.Awesome.Challenge.Online.Marketplace.API.secuirity.config.ApplicationConfig.getCurrentUser;
import static com.Awesome.Challenge.Online.Marketplace.API.secuirity.config.ApplicationConfig.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final ApplicationConfig currentLogin;
    private final ProductService productService;

    // This method implement the logic of submintting product order for current logged-in user
    public ResponseEntity<?> pressOrder(OrderDto orderDto){

        Integer buyerId = getCurrentUserId();

        Integer productId = orderDto.getProductId();

        // Check if the User with the provided Id exists
        User user = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Try to log in before order"));

        // Check if the Product with the provided Id exists
        Product product  = productRepository.findById(productId)
                .orElseThrow(()->new RuntimeException("Product not found with : "+ productId));

        // Check if the chosen quantity exceeds the available quantity
        if (orderDto.getQuantity() > product.getQuantity()) {
            return ResponseEntity.badRequest().body("Desired quantity exceeds available quantity for product with ID: " + productId);
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
//                .orderStatus(orderDto.getOrderStatus())
                .orderStatus(OrderStatus.PENDING)
                .orderDate(orderDto.getOrderDate())
                .build();
                orderRepository.save(order);
                return ResponseEntity.ok(order);

    }
    //This method checks the user's role and ensures that only authorized users can update orders.
    public Order updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null.");
        }

        // Get the currently logged-in user
        User loggedInUser = getCurrentUser();

        if (loggedInUser != null && loggedInUser.getRole().equals(Role.SELLER)) {
            // Get orders associated with the logged-in seller
            List<Order> sellerOrders = getOrdersForLoggedInSeller();

            // Check if the order to be updated belongs to the logged-in seller
            Optional<Order> orderToUpdate = sellerOrders.stream()
                    .filter(order -> order.getId().equals(orderId))
                    .findFirst();

            if (orderToUpdate.isPresent()) {
                // Update the order status
                Order order = orderToUpdate.get();
                order.setOrderStatus(newStatus);
                return orderRepository.save(order);
            } else {
                throw new UnauthorizedAccessException("User is not authorized to update this order.");
            }
        } else if (loggedInUser != null && loggedInUser.getRole().equals(Role.ADMIN)) {
            // If the user is an admin, update the order regardless of seller association
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

            order.setOrderStatus(newStatus);
            return orderRepository.save(order);
        } else {
            throw new UnauthorizedAccessException("User is not authorized to update orders.");
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
}
