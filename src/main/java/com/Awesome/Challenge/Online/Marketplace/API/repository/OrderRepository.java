package com.Awesome.Challenge.Online.Marketplace.API.repository;

import com.Awesome.Challenge.Online.Marketplace.API.model.Order;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
//    List<Order> findByUserId(Integer userId);
List<Order> findByProductIn(List<Product> products);

@Query("SELECT o FROM Order o WHERE o.buyer.id = ?1")
List<Order> getOrderForBuyer(Integer id);
}

