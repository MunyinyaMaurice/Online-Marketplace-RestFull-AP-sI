
package com.Awesome.Challenge.Online.Marketplace.API.service;

import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductDto;
import com.Awesome.Challenge.Online.Marketplace.API.dto.ProductWithImageDataDto;
import com.Awesome.Challenge.Online.Marketplace.API.model.Category;
import com.Awesome.Challenge.Online.Marketplace.API.model.Product;
import com.Awesome.Challenge.Online.Marketplace.API.model.ProductImage;
import com.Awesome.Challenge.Online.Marketplace.API.model.User;
import com.Awesome.Challenge.Online.Marketplace.API.repository.CategoryRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductImageRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.ProductRepository;
import com.Awesome.Challenge.Online.Marketplace.API.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static com.Awesome.Challenge.Online.Marketplace.API.secuirity.config.ApplicationConfig.getCurrentUser;


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
    public Product createProduct(ProductDto productDto) {
        try {
            User seller = getCurrentUser();

            // Extract the categoryId from the ProductDto
            Integer categoryId = productDto.getCategoryId();

            // Check if the Category with the provided categoryId exists
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found for categoryId: " + categoryId));

            // Create the Product entity with the retrieved Category and User (seller)
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
        } catch (EntityNotFoundException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create product. Please try again later.", ex);
        }
    }

    public Product updateProduct(Integer productId, ProductDto productDto) {
        // Retrieve the existing Product entity from the database
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found for id: " + productId));

        // Update the fields of the existing Product entity with the new values from the ProductDto
        if (productDto.getName() != null) {
            existingProduct.setName(productDto.getName());
        }
        if (productDto.getDescription() != null) {
            existingProduct.setDescription(productDto.getDescription());
        }
        if (productDto.getPrice() != null) {
            existingProduct.setPrice(productDto.getPrice());
        }
        if (productDto.getQuantity() != null) {
            existingProduct.setQuantity(productDto.getQuantity());
        }
        if (productDto.getCategoryId() != null) {
            // Fetch the Category entity from the database based on the provided categoryId
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found for categoryId: " + productDto.getCategoryId()));
            existingProduct.setCategory(category);
        }
        if (productDto.getSellerId() != null) {
            // Fetch the User (seller) entity from the database based on the provided sellerId
            User seller = userRepository.findById(productDto.getSellerId())
                    .orElseThrow(() -> new RuntimeException("User (seller) not found for sellerId: " + productDto.getSellerId()));
            existingProduct.setSeller(seller);
        }

        // Save and return the updated Product entity
        return productRepository.save(existingProduct);
    }
    // Find a product by its ID
    public Product findProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found for id: " + productId));
    }

    public ProductWithImageDataDto getProductWithImageData(Integer productId) {
        try {
            // Retrieve the product from the database
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

            // Retrieve the associated image data
            List<ProductImage> productImages = productImageRepository.findByProductId(productId);

            // Create a new DTO to hold product data with image data
            ProductWithImageDataDto productWithImageDataDto = new ProductWithImageDataDto();
            productWithImageDataDto.setId(product.getId());
            productWithImageDataDto.setName(product.getName());
            productWithImageDataDto.setDescription(product.getDescription());
            productWithImageDataDto.setPrice(product.getPrice());
            productWithImageDataDto.setQuantity(product.getQuantity());
            productWithImageDataDto.setSellerId(product.getSeller());
            productWithImageDataDto.setCategoryId(product.getCategory());
            productWithImageDataDto.setDateCreated(product.getDateCreated());
            productWithImageDataDto.setLastUpdated(product.getLastUpdated());

            // Convert image data to Base64 for easy transmission
            List<String> imageDataList = productImages.stream()
                    .map(productImage -> Base64.getEncoder().encodeToString(productImage.getImageData()))
                    .collect(Collectors.toList());
            productWithImageDataDto.setImageDataList(imageDataList);

            return productWithImageDataDto;
        } catch (Exception e) {
            logger.error("Failed to get product data with image for product ID: {}", productId, e);
            throw new RuntimeException("Failed to get product data with image for product ID: " + productId);
        }
    }

    // This method Delete a product by its ID
    public void deleteProductById(Integer productId) {
        if(productId == null){
            throw new IllegalArgumentException("Product ID cannot be null.");
        }
         Product product = findProductById(productId);

    try{
        productRepository.delete(product);
    }catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Failed to delete product. Please try again later.");
    }
    }
    // This method Get all method
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
