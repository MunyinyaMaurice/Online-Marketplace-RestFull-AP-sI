package com.Awesome.Challenge.Online.Marketplace.API.dto;

import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

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

//    @NotNull
//    private Integer userId;

    @NotNull
    private Integer productId;

    private Date createdAt;

    private Date updatedAt;
}
