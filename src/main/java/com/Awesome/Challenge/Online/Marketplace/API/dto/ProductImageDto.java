package com.Awesome.Challenge.Online.Marketplace.API.dto;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDto {

    private String imageData;

    private Integer productId;
}
