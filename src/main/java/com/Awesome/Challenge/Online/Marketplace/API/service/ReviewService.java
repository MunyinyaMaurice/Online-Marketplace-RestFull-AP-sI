package com.Awesome.Challenge.Online.Marketplace.API.service;


import com.Awesome.Challenge.Online.Marketplace.API.dto.ReviewDto;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import com.Awesome.Challenge.Online.Marketplace.API.model.Review;
import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ReviewRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.Awesome.Challenge.Online.Marketplace.API.secuirity.config.ApplicationConfig.getCurrentUser;
import static com.Awesome.Challenge.Online.Marketplace.API.secuirity.config.ApplicationConfig.getCurrentUserId;

@Service
@RequiredArgsConstructor
public class ReviewService {
    @Autowired
    private final ReviewRepository reviewRepository;

    @Autowired
    private  final UserRepository userRepository;

    @Autowired
    private final ProductRepository productRepository;

    public Review submitReview(ReviewDto reviewDto){

        User user= getCurrentUser();
        Integer productId = reviewDto.getProductId();


        // Check if the Product with the provided Id exists
        Product product  = productRepository.findById(productId)
                .orElseThrow(()->new RuntimeException("Product not found with : "+ productId));

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(reviewDto.getRating())
                .comment(reviewDto.getComment())
                .createdAt(reviewDto.getCreatedAt())
                .updatedAt(reviewDto.getUpdatedAt())
                        .build();
        return reviewRepository.save(review);
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
