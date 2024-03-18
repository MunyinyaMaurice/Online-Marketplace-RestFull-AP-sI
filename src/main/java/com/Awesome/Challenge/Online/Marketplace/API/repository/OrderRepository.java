package com.Awesome.Challenge.Online.Marketplace.API.repository;

import com.Awesome.Challenge.Online.Marketplace.API.model.Order;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Integer> {
//    List<Order> findByUserId(Integer userId);
List<Order> findByProductIn(List<Product> products);
}

