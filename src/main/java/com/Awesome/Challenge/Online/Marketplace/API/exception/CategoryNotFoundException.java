package com.Awesome.Challenge.Online.Marketplace.API.exception;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends RuntimeException {

  private final int categoryId;

  public CategoryNotFoundException(String message) {
    super(message);
    this.categoryId = -1; // Or any default value for category id if not available
  }

  public CategoryNotFoundException(String message, int categoryId) {
    super(message);
    this.categoryId = categoryId;
  }

  public int getCategoryId() {
    return categoryId;
  }

  public HttpStatus getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }
    
}
