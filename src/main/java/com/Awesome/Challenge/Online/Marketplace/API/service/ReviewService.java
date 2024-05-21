package com.Awesome.Challenge.Online.Marketplace.API.service;


import com.Awesome.Challenge.Online.Marketplace.API.dto.ReviewDto;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ApplicationException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorCode;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorResponse;
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

    public Review submitReview(Integer productId ,ReviewDto reviewDto){
        try {
        User user= getCurrentUser();
        // Check if the Product with the provided Id exists
        Product product  = productRepository.findById(productId)
                .orElseThrow(()->new ApplicationException(ErrorCode.NOT_FOUND));

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                        .build();
         return reviewRepository.save(review);
    }
        catch (ApplicationException e){
           throw new ApplicationException(ErrorCode.SERVER_ERROR);
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
