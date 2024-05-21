package com.Awesome.Challenge.Online.Marketplace.API.repository;

import com.Awesome.Challenge.Online.Marketplace.API.dto.UserDetailsDTO;
import com.Awesome.Challenge.Online.Marketplace.API.model.User;

import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
    public boolean existsByEmail (String email);
    
    @Query("SELECT new com.Awesome.Challenge.Online.Marketplace.API.dto.UserDetailsDTO(u.firstName, u.lastName, u.email, u.role) FROM User u")
public List<UserDetailsDTO> getAllUserDetails();
    

}
