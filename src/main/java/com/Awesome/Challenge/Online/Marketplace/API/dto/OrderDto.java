package com.Awesome.Challenge.Online.Marketplace.API.dto;

import com.Awesome.Challenge.Online.Marketplace.API.model.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @Valid
    @NotNull(message = "product quality shouldn't be null")
    @Positive(message = "product quality should be positive")
    private Integer quantity;
    private OrderStatus orderStatus;
}
