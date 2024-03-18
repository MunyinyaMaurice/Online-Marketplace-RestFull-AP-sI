package com.Awesome.Challenge.Online.Marketplace.API.repository;

import com.Awesome.Challenge.Online.Marketplace.API.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Integer> {
    List<Review> findByProductId(Integer productId);
}
