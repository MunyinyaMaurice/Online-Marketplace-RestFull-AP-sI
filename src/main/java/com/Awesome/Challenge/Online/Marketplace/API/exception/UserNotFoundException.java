package com.Awesome.Challenge.Online.Marketplace.API.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException  extends RuntimeException {

  private final int sellerId;

  public UserNotFoundException(String message) {
    super(message);
    this.sellerId = -1; // Or any default value for seller id if not available
  }

  public UserNotFoundException(String message, int sellerId) {
    super(message);
    this.sellerId = sellerId;
  }

  public int getSellerId() {
    return sellerId;
  }

  public HttpStatus getStatusCode() {
    return HttpStatus.NOT_FOUND;
  }
    
}
