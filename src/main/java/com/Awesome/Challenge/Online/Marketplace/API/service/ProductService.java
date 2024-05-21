
package com.Awesome.Challenge.Online.Marketplace.API.service;

import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductDto;
import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductImageDto;
import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductUpdateDto;
import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductWithImageDataDto;
// import com.Awesome.Challenge.Online.Marketplace.API.exception.UserNotFoundException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ApplicationException;
import com.Awesome.Challenge.Online.Marketplace.API.exceptionHandler.ErrorCode;
import com.Awesome.Challenge.Online.Marketplace.API.model.Category;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import com.Awesome.Challenge.Online.Marketplace.API.model.ProductImage;
import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import com.Awesome.Challenge.Online.Marketplace.API.repository.CategoryRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductImageRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.Awesome.Challenge.Online.Marketplace.API.security.config.ApplicationConfig.getCurrentUser;


@Service
//@RequiredArgsConstructor
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private  final UserRepository userRepository;
    @Autowired
    private final ProductImageRepository productImageRepository;


    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository,
                          UserRepository userRepository, ProductImageRepository productImageRepository,
                          ProductWithImageDataDto productWithImageDataDto) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.productImageRepository = productImageRepository;

    }
    // This method create new product, associate it to seller. sets the listed flag based on the quantity provided =>true >0
    public Product createProduct( ProductDto productDto) {
        User seller = getCurrentUser();
        if (productRepository.existsByName(productDto.getName())) {
            throw new ApplicationException(ErrorCode.CONFLICT);
        }
            // Extract the categoryId from the ProductDto
            Integer categoryId = productDto.getCategoryId();

            // Check if the Category with the provided categoryId exists
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND, "Category not found"));

            // Validate price and quantity
            if (!isValidNumericValue(productDto.getPrice()) || !isValidNumericValue(productDto.getQuantity())) {
                throw new ApplicationException(ErrorCode.BAD_REQUEST, "Price and quantity must be numeric values.");
            }
            // Create the Product entity with the retrieved Category and User as (seller)
            try {
                Product product = Product.builder()
                        .name(productDto.getName())
                        .description(productDto.getDescription())
                        .price(productDto.getPrice())
                        .quantity(productDto.getQuantity())
                        .category(category)
                        .seller(seller)
                        .build();

                // Set the listed flag based on the quantity
                product.setListed(productDto.getQuantity() > 0);

                // Save the Product entity
                return productRepository.save(product);

            } catch (ApplicationException ex) {
                throw new ApplicationException(ErrorCode.SERVER_ERROR);
            }
    }
    private boolean isValidNumericValue(BigDecimal value) {
        try {
            if (value != null) {
                new BigDecimal(value.toString()); // Attempt to create a BigDecimal from its string representation
                return true; // If parsing succeeds, the value is numeric
            } else {
                return false; // Null values are not considered numeric
            }
        } catch (NumberFormatException ex) {
            // return false; 
            throw new IllegalArgumentException("Invalid numeric value: " + value);
        }
    }
    
    
    private boolean isValidNumericValue(Integer value) {
        try {
            Integer.parseInt(String.valueOf(value)); // Attempt to parse the integer value as a string
            return true; // If parsing succeeds, the value is a valid integer
        } catch (NumberFormatException ex) {
            return false; // If parsing fails, the value is not a valid integer
        }
    }

        public Product updateProduct(Integer productId, ProductUpdateDto productDto) {
                try {
                // Retrieve the existing Product entity from the database
                Product existingProduct = productRepository.findById(productId)
                        .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
        
                // Update the fields of the existing Product entity with the new values from the ProductDto
        
                // Validate and update product name if provided
                if (productDto.getName() != null) {
                    if (!productRepository.existsByName(productDto.getName())) {
                        existingProduct.setName(productDto.getName());
                    } else {
                       throw new ApplicationException(ErrorCode.CONFLICT);
                    }
                }
        
                // Validate and update product description if provided
                if (productDto.getDescription() != null) {
                    existingProduct.setDescription(productDto.getDescription());
                }
        
                // Validate and update product price if provided
                if (productDto.getPrice() != null) {
                    if (productDto.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                        existingProduct.setPrice(productDto.getPrice());
                    } else {
                        throw new ApplicationException(ErrorCode.BAD_REQUEST,"A product price should be greater than 0.");
                    }
                }
        
                // Validate and update product quantity if provided
                if (productDto.getQuantity() != null) {
//                    if (productDto.getQuantity() > 0) {
                        existingProduct.setQuantity(productDto.getQuantity());
//                    } else {
//                        throw new ApplicationException(ErrorCode.BAD_REQUEST,"Quantity must be greater than 0.");
//                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//                        }
                }
        
                // Validate and update product category if provided
                if (productDto.getCategoryId() != null) {
                    if (categoryRepository.existsById(productDto.getCategoryId())) {
                        Category category = categoryRepository.findById(productDto.getCategoryId())
                                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
                        existingProduct.setCategory(category);
                    } else {
                        throw new ApplicationException(ErrorCode.BAD_REQUEST,"provided category does not exist");
                    }
                }
        
                // Validate and update product seller if provided
                if (productDto.getSellerId() != null) {
                    if (userRepository.existsById(productDto.getSellerId())) {
                        User seller = userRepository.findById(productDto.getSellerId())
                                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND,"User (seller) not found"));
                        existingProduct.setSeller(seller);
                    } else {
                        throw new ApplicationException(ErrorCode.BAD_REQUEST);
                                
                    }
                }
                // Save and return the updated Product entity
                 return productRepository.save(existingProduct);

        
        } catch (EntityNotFoundException ex) {
            throw new ApplicationException(ErrorCode.SERVER_ERROR);
        }
        }
       
    // Find a product by its ID
    public Product findProductById(Integer productId) {

            return productRepository.findById(productId)
                    .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
    }
    public ProductWithImageDataDto getProductWithImageData(Integer productId) {
        Map<String, Object> response = new HashMap<>();
        // Check if the product exists
        if (!productRepository.existsById(productId)) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST, "Product does not exist");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
        // Retrieve the associated image data
        try {
            List<ProductImage> productImages = productImageRepository.findByProductId(productId);

            // Created a new DTO to hold product data with image data
            ProductWithImageDataDto productWithImageDataDto = new ProductWithImageDataDto();
            productWithImageDataDto.setId(product.getId());
            productWithImageDataDto.setName(product.getName());
            productWithImageDataDto.setDescription(product.getDescription());
            productWithImageDataDto.setPrice(product.getPrice());
            productWithImageDataDto.setQuantity(product.getQuantity());

            // Convert image data to Base64 for easy transmission
            List<String> imageDataList = productImages.stream()
                    .map(productImage -> Base64.getEncoder().encodeToString(productImage.getImageData()))
                    .collect(Collectors.toList());
            productWithImageDataDto.setImageDataList(imageDataList);
                return productWithImageDataDto;
//
        } catch (ApplicationException ex) {
            throw new ApplicationException(ErrorCode.SERVER_ERROR);
        }
    }
    
    // This method Delete a product by its ID
    
    public void deleteProductById(Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null.");
        }
    
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND));
        productRepository.delete(product);
    }
    
    // This method Get all product
    public List<Product> getALlProduct() {
        return productRepository.findAll();
    }

    // Display All product which has quantity > zero.
    public List<Product> getListedProducts() {
        return productRepository.findByListedTrue();
    }
    // Display All product which has quantity == zero && listed = false.
    public List<Product> getNotListedProducts() {
        return productRepository.findNotListedProducts();
    }
    // Find product buy name

    // Retrieve products sorted by average rating
    public List<Product> getProductsSortedByAverageRating() {
        return productRepository.findAllSortedByAverageRating();
    }

    // Retrieve products with a minimum average rating of 4.0
    public List<Product> getHighRatedProducts(double minRating) {
        return productRepository.findAllWithMinAverageRating(minRating);
    }

    // Retrieve products sorted by total number of reviews
    public List<Product> getPopularProducts() {
        return productRepository.findAllSortedByTotalReviews();
    }

    // Search produce by name or price which is in range of price <= || => 10%
    public List<Product> searchProducts(String searchParam) {
        return productRepository.searchByProductName(searchParam);
    }

    public List<Product> getProductsBySeller(Integer sellerId) {
        return productRepository.findBySellerId(sellerId);
    }
}
