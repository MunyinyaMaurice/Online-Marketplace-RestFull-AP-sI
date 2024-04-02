package com.Awesome.Challenge.Online.Marketplace.API.dto;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto {

    private String name;

    private String description;

    private BigDecimal price;
 
    private Integer quantity;

    private Integer categoryId;
        
    private Integer sellerId;

}