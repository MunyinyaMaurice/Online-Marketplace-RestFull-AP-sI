package com.Awesome.Challenge.Online.Marketplace.API.dto;

import com.Awesome.Challenge.Online.Marketplace.API.model.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @Valid
//    @NotNull(message = "User Id shouldn't be null")
//    private Integer buyerId;

    @NotNull(message = "productId shouldn't be null")
    private Integer productId;
    @NotNull(message = "product quality shouldn't be null")
    private Integer quantity;

    private BigDecimal totalPrice;

    private OrderStatus orderStatus;

    private Date orderDate;

}
