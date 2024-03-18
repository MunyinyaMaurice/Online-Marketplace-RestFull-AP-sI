package com.Awesome.Challenge.Online.Marketplace.API.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Awesome.Challenge.Online.Marketplace.API.model.ProductImage;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage,Integer> {
//    List<ProductImage> findByProductIDFromImage(Integer productId);
    List<ProductImage> findByProductId(Integer productId);
}
