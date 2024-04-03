package com.Awesome.Challenge.Online.Marketplace.API.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

    @Valid

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    private String comment;
}
