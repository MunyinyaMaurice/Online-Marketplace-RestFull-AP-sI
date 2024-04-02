package com.Awesome.Challenge.Online.Marketplace.API.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    Optional<Product> findByName(String name);

    // Custom query method to get listed products
    List<Product> findByListedTrue();

    // Custom query method to get not listed products
    @Query("SELECT p FROM Product p WHERE p.quantity = 0 AND p.listed = false")
    List<Product> findNotListedProducts();

    // Custom query method to get products sorted by average rating
    @Query("SELECT p FROM Product p JOIN p.reviews r GROUP BY p.id ORDER BY AVG(r.rating) DESC")
    List<Product> findAllSortedByAverageRating();

    // Custom query method to get products with a minimum average rating
    @Query("SELECT p FROM Product p JOIN p.reviews r GROUP BY p.id HAVING AVG(r.rating) >= :minRating")
    List<Product> findAllWithMinAverageRating(double minRating);

    // Custom query method to get products sorted by total number of reviews
    @Query("SELECT p FROM Product p JOIN p.reviews r GROUP BY p.id ORDER BY COUNT(r) DESC")
    List<Product> findAllSortedByTotalReviews();

    List<Product>findBySellerId(Integer sellerId);

    @Query("SELECT p FROM Product p WHERE p.listed = true AND p.name LIKE CONCAT('%', :searchParam, '%')")
    List<Product> searchByProductName(@Param("searchParam") String searchParam);

    boolean existsByName(String name);


}
