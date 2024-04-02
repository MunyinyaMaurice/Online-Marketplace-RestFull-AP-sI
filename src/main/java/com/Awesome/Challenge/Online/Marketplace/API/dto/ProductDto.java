package com.Awesome.Challenge.Online.Marketplace.API.dto;

//import com.Awesome.Challenge.Online.Marketplace.API.model.Category;
//import com.Awesome.Challenge.Online.Marketplace.API.model.Order;
//import com.Awesome.Challenge.Online.Marketplace.API.model.Review;
//import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
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
        @NotBlank(message = "Product name shouldn't be blank")
    private String name;

    @NotNull(message = "Product description shouldn't be null")
    private String description;

     @NotNull(message = "Product price shouldn't be null")
    @DecimalMin(value = "0.01", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;

    @Positive 
    private Integer quantity;

    @NotNull(message = "Category ID shouldn't be null")
    private Integer categoryId;
        // @NotNull(message = "seller ID shouldn't be null")
        private Integer sellerId;

        // private Date dateCreated;

        // private Date lastUpdated;
}
