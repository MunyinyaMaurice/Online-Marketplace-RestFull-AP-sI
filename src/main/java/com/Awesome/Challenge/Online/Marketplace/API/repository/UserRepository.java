package com.Awesome.Challenge.Online.Marketplace.API.repository;

import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
    public boolean existsByEmail (String email);
}
