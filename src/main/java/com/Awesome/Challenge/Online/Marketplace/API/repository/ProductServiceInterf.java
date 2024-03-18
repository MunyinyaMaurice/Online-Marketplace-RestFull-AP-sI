package com.Awesome.Challenge.Online.Marketplace.API.repository;

import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductServiceInterf {
    void uploadImage(ProductImageDto productImageDto) throws IOException;
//    String getImageData(Integer productId, Integer imageId);
//    void deleteImage(Integer productId, Integer imageId);
}
