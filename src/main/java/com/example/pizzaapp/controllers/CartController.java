package com.example.pizzaapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.pizzaapp.dtos.CartItemDto;
import com.example.pizzaapp.services.CartService;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/cart")
    public String cart(Model model) {
        return "pages/cart/index";
    }

    /**
     * API endpoint to validate and clean cart items
     * Returns valid items and count of removed items
     */
    @PostMapping("/api/cart/validate")
    @ResponseBody
    public ResponseEntity<?> validateCart(@RequestBody String cartJson) {
        try {
            List<CartItemDto> cartItems = cartService.parseCartItems(cartJson);
            List<CartItemDto> validCartItems = cartService.validateAndCleanCartItems(cartItems);
            int invalidItemsCount = cartService.getInvalidItemsCount(cartItems, validCartItems);

            return ResponseEntity.ok().body(new CartValidationResponse(validCartItems, invalidItemsCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to validate cart: " + e.getMessage());
        }
    }

    /**
     * Response class for cart validation
     */
    public static class CartValidationResponse {
        public final List<CartItemDto> validItems;
        public final int removedItemsCount;
        public final String message;

        public CartValidationResponse(List<CartItemDto> validItems, int removedItemsCount) {
            this.validItems = validItems;
            this.removedItemsCount = removedItemsCount;
            this.message = removedItemsCount > 0 
                ? String.format("%d item(s) were removed because they are no longer available.", removedItemsCount)
                : "All cart items are valid.";
        }
    }
}
