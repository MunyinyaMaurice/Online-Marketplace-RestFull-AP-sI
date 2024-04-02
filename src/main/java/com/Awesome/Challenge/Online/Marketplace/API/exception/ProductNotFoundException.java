package com.Awesome.Challenge.Online.Marketplace.API.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends RuntimeException {

  private final int productId;

  public ProductNotFoundException(String message) {
    super(message);
    this.productId = -1; // Or any default value for product id if not available
  }

  public ProductNotFoundException(String message, int productId) {
    super(message);
    this.productId = productId;
  }

  public int getProductId() {
    return productId;
  }

  // New method to define the status code
  public HttpStatus getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }
}
