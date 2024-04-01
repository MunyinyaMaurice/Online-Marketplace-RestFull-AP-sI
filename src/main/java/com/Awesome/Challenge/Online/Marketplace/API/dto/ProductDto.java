package com.Awesome.Challenge.Online.Marketplace.API.dto;

//import com.Awesome.Challenge.Online.Marketplace.API.model.Category;
//import com.Awesome.Challenge.Online.Marketplace.API.model.Order;
//import com.Awesome.Challenge.Online.Marketplace.API.model.Review;
//import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
        @Valid
        @NotNull(message = "product name shouldn't be null")
        private String name;

        @NotNull(message = "product description shouldn't be null")
        private String description;

        @NotNull(message = "product price shouldn't be null")
        private BigDecimal price;

        @NotNull(message = "product quantity shouldn't be null")
        private Integer quantity;

        @NotNull(message = "category ID shouldn't be null")
        private Integer categoryId;

        // @NotNull(message = "seller ID shouldn't be null")
        private Integer sellerId;

        private Date dateCreated;

        private Date lastUpdated;
}
