package com.Awesome.Challenge.Online.Marketplace.API.controller;

import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductImageDto;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import com.Awesome.Challenge.Online.Marketplace.API.service.ProductImageServiceImpl;
import com.Awesome.Challenge.Online.Marketplace.API.service.ProductService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;


@RestController
//@RequiredArgsConstructor
@RequestMapping("/api/images")

@Tag(name = "Manage Product and Image association")
public class ProductImageController {

    private final ProductImageServiceImpl productImageService;
    @Autowired
    public ProductImageController(ProductImageServiceImpl productImageService, ProductService productService) {
        this.productImageService = productImageService;
    }
    @Operation(
        summary = "Upload new product image [jpeg, jpg, and gif] files are allowed",
            description = "Endpoint to upload a new image by proving the product id to be associated to.",
                responses = {
                    @ApiResponse(
                            description = "Image uploaded successfully.",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }
    )
    //This End point helps to upload images for specified product && store them in DB as bytes "MEDIUMBLOB"
    
    @PostMapping("/upload/{productId}")
    public ResponseEntity<?> uploadImage(@PathVariable Integer productId,
                                         @RequestParam("file") MultipartFile file) {
        try {
            // Check if the file is not empty
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Image file is empty.");
            }

            // Check if the file has an allowed extension
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            if (!isImageFile(fileName)) {
                return ResponseEntity.badRequest().body("Only jpeg, jpg, and gif files are allowed.");
            }

            ProductImageDto productImageDto = new ProductImageDto();
            productImageDto.setProductId(productId);

            // Extract base64 encoded image data from the MultipartFile
            byte[] imageData = file.getBytes();
            String ImageData = Base64.getEncoder().encodeToString(imageData);
            productImageDto.setImageData(ImageData);

            productImageService.uploadImage(productImageDto);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
        }

    }
    // This method helper checks if the file extension is one of the allowed image types (jpeg, jpg, gif).
    private boolean isImageFile(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        return extension != null && (extension.equalsIgnoreCase("jpeg") ||
                extension.equalsIgnoreCase("jpg") ||
                extension.equalsIgnoreCase("gif"));
    }

    @Operation(summary = "Delete product image",description = "Delete product image by providing image id")
    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Integer imageId) {
        try {
            productImageService.deleteImage( imageId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage().getBytes());
        }
    }


}
