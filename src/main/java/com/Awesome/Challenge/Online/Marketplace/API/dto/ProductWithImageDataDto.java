package com.Awesome.Challenge.Online.Marketplace.API.dto;

import com.Awesome.Challenge.Online.Marketplace.API.model.Category;
import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

// This Component helps while retrieving the product and the list of image associated.
@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithImageDataDto {
    private Integer id;
    private String name;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private Category categoryId;

    private User sellerId;

    private Date dateCreated;

    private Date lastUpdated;
    private List<String> imageDataList;
}
