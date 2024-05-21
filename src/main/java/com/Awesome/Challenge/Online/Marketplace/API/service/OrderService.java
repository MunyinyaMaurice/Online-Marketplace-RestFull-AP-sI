package com.Awesome.Challenge.Online.Marketplace.API.service;

import com.Awesome.Challenge.Online.Marketplace.API.dto.OrderDto;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ApplicationException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorCode;
import com.Awesome.Challenge.Online.Marketplace.API.model.*;
import com.Awesome.Challenge.Online.Marketplace.API.repository.OrderRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.Awesome.Challenge.Online.Marketplace.API.security.config.ApplicationConfig.getCurrentUser;
import static com.Awesome.Challenge.Online.Marketplace.API.security.config.ApplicationConfig.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    private final ProductRepository productRepository;
    private final ProductService productService;

    // This method implement the logic of submintting product order for current logged-in user
    public Order submitOrder(Integer productId, OrderDto orderDto) {
        try {
            Integer buyerId = getCurrentUserId();

            // Check if the User with the provided Id exists
            User user = userRepository.findById(buyerId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.BAD_REQUEST, "Try to log in before order"));
            // Check if the Product with the provided Id exists
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, "Product not found "));

            // Check if the chosen quantity exceeds the available quantity
            if (orderDto.getQuantity() > product.getQuantity()) {

                throw new ApplicationException(ErrorCode.BAD_REQUEST, "Requested quantity is not available");
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
            return orderRepository.save(order);
        } catch (ApplicationException ex) {
            throw new ApplicationException(ErrorCode.SERVER_ERROR);
        }

    }

    //This method checks the user's role and ensures that only authorized users can update orders.
    // public ResponseEntity<?> updateOrderStatus(Integer orderId, OrderStatus newStatus, BindingResult bindingResult) {


    public Order updateOrderStatus(Integer orderId, Map<String, String> requestBody) {
        User loggedInUser = getCurrentUser();
        if (loggedInUser == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED, "User is not logged in.");
        }

        if (!orderRepository.existsById(orderId)) {
            throw new ApplicationException(ErrorCode.NOT_FOUND);
        }
        // Get the order status from the request body
        String orderStatus = requestBody.get("orderStatus");

        // Check if the orderStatus is null or empty
        if (orderStatus == null || orderStatus.isEmpty()) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST, "Order status is required.");
        }
        try {
            // Convert the orderStatus string to OrderStatus enum
            OrderStatus newStatus = OrderStatus.valueOf(orderStatus);

            // Check if the user is authorized to update orders

            if (loggedInUser.getRole().equals(Role.SELLER)) {
                List<Order> sellerOrders = getOrdersForLoggedInSeller();

                Optional<Order> orderToUpdate = sellerOrders.stream()
                        .filter(order -> order.getId().equals(orderId))
                        .findFirst();

                try {
                    if (orderToUpdate.isPresent()) {
                        Order order = orderToUpdate.get();
                        order.setOrderStatus(newStatus);
                        return orderRepository.save(order);
                    }
                } catch (ApplicationException e) {
                    throw new ApplicationException(ErrorCode.SERVER_ERROR);
                }
            }
                if (loggedInUser.getRole().equals(Role.ADMIN)) {
                    Order order = orderRepository.findById(orderId)
                            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
                    try {
                        order.setOrderStatus(newStatus);
                        return orderRepository.save(order);
                    } catch (ApplicationException e) {
                        throw new ApplicationException(ErrorCode.SERVER_ERROR);
                    }
                }
                throw new ApplicationException(ErrorCode.UNAUTHORIZED);
            } catch(ApplicationException ex){
                throw new ApplicationException(ErrorCode.SERVER_ERROR);
            }
        }

    //This method check if user with SELLER role has any order on product associated to them if user is ADMIN all orders are listed
    public List<Order> getOrdersForLoggedInSeller() {
        User loggedInUser = getCurrentUser();
        if (loggedInUser == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED,"User not logged in");
        }
        if (loggedInUser.getRole().equals(Role.SELLER)) {
            List<Product> sellerProducts = productService.getProductsBySeller(loggedInUser.getId());
            return orderRepository.findByProductIn(sellerProducts);
        } else if (loggedInUser.getRole().equals(Role.ADMIN)) {
            return orderRepository.findAll();
        } else {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED,"User is not authorized to access this resource");
        }
    }
    // public ResponseEntity<?> getOrdersForBuyer(BindingResult bindingResult) {
        public List<Order> getOrdersForBuyer() {

            User loggedInUser = getCurrentUser();
            if (loggedInUser == null) {
                throw new ApplicationException(ErrorCode.UNAUTHORIZED,"User not logged in");
            }
        try {
            return orderRepository.getOrderForBuyer(loggedInUser.getId());

    }  catch (ApplicationException ex) {
            throw new ApplicationException(ErrorCode.SERVER_ERROR);
        }
}
}