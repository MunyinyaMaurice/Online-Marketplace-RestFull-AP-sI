package com.Awesome.Challenge.Online.Marketplace.API.dto;


import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

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

    @Positive(message = "Quantity must be greater than zero") 
    private Integer quantity;

    @NotNull(message = "Category ID shouldn't be null")
    private Integer categoryId;
        }
