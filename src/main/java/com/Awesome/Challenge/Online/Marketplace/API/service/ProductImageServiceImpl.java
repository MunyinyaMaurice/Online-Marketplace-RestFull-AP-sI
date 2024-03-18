package com.Awesome.Challenge.Online.Marketplace.API.service;

import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductImageDto;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import com.Awesome.Challenge.Online.Marketplace.API.model.ProductImage;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductImageRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductServiceInterf;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductServiceInterf {
    private static final Logger logger = LoggerFactory.getLogger(ProductImageServiceImpl.class);

    private final ProductImageRepository productImageRepository;

    private final ProductRepository productRepository;

    //This method implement logic of uploading images for specified product && store them in DB as bytes "MEDIUMBLOB"
    public void uploadImage(ProductImageDto productImageDto) {

        Integer productId = productImageDto.getProductId();
        // Retrieve the product from the database
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Decode the base64 encoded image data
        byte[] decodedImageData = Base64.getDecoder().decode(productImageDto.getImageData());

        // Create a ProductImage entity
        ProductImage productImage = ProductImage.builder()
                .imageData(decodedImageData)
                .product(product)
                .build();

        // Save the product image entity
        productImageRepository.save(productImage);
    }

    public Product getProductWithImages(Integer productId) {
        try {
            Optional<Product> productOptional = productRepository.findById(productId);

            if (!productOptional.isPresent()) {
                // Consider returning a specific error response or throwing a different exception
                throw new RuntimeException("Product not found with ID: " + productId);
            }

            Product product = productOptional.get();

            // Fetch associated images for this product
            List<ProductImage> images = productImageRepository.findByProductId(productId);
            product.setImages(images);

            return product;
        } catch (Exception e) {
//            log.error("Error retrieving product data: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve product data", e);
        }
    }


    public void deleteImage(Integer imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Product image not found with id: " + imageId));
        productImageRepository.delete(productImage);


    }
}

