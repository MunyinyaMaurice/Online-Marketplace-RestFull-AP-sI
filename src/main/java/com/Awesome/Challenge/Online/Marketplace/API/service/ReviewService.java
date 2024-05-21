package com.Awesome.Challenge.Online.Marketplace.API.service;


import com.Awesome.Challenge.Online.Marketplace.API.dto.ReviewDto;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import com.Awesome.Challenge.Online.Marketplace.API.model.Review;
import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ReviewRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.List;

import static com.Awesome.Challenge.Online.Marketplace.API.security.config.ApplicationConfig.getCurrentUser;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ResponseEntity<Map<String, Object>> submitReview(Integer productId ,@Valid ReviewDto reviewDto, BindingResult bindingResult){
        Map<String, Object> response = new HashMap<>();
        try {
        User user= getCurrentUser();
        // Check if the Product with the provided Id exists
        Product product  = productRepository.findById(productId)
                .orElseThrow(()->new EntityNotFoundException("Product not found with : "+ productId));

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                        .build();
        // return reviewRepository.save(review);
        response.put("productReview", reviewRepository.save(review));
        return ResponseEntity.ok(response);
    }
        catch (EntityNotFoundException e){
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        catch (Exception e){
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }
    public List<Review> getProductReviews(Integer productId) {
        // Retrieve reviews associated with the product ID
        return reviewRepository.findByProductId(productId);
    }

    // Method to calculate the average rating for a product
    public double getProductAverageRating(Integer productId) {
        // Retrieve reviews associated with the product ID
        List<Review> reviews = reviewRepository.findByProductId(productId);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        // Calculate the total sum of ratings
        int totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }

        // Calculate the average rating
        return (double) totalRating / reviews.size();
    }
}
