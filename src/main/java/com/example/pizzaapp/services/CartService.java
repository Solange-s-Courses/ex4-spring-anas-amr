package com.example.pizzaapp.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.pizzaapp.dtos.CartItemDto;
import com.example.pizzaapp.models.Topping;
import com.example.pizzaapp.repositories.ProductRepository;
import com.example.pizzaapp.repositories.ProductOptionRepository;
import com.example.pizzaapp.repositories.ToppingRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CartService {

    private final ObjectMapper objectMapper;
    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ToppingRepository toppingRepository;

    public CartService(ObjectMapper objectMapper, ProductRepository productRepository,
                      ProductOptionRepository productOptionRepository, ToppingRepository toppingRepository) {
        this.objectMapper = objectMapper;
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
        this.toppingRepository = toppingRepository;
    }

    public List<CartItemDto> parseCartItems(String cartJson) throws Exception {
        return objectMapper.readValue(cartJson, new TypeReference<List<CartItemDto>>() {
        });
    }

    public boolean isCartEmpty(List<CartItemDto> cartItems) {
        return cartItems == null || cartItems.isEmpty();
    }

    /**
     * Validates and cleans up cart items by removing invalid products/options
     * @param cartItems List of cart items to validate
     * @return List of valid cart items
     */
    public List<CartItemDto> validateAndCleanCartItems(List<CartItemDto> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return cartItems;
        }

        return cartItems.stream()
                .filter(this::isValidCartItem)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a cart item is valid (product exists, size available, toppings exist)
     */
    private boolean isValidCartItem(CartItemDto cartItem) {
        try {
            // Check if product exists
            if (!productRepository.existsById(cartItem.getProductId())) {
                return false;
            }

            // Check if product option (size) exists
            if (!productOptionRepository.findByProductIdAndSize(cartItem.getProductId(), cartItem.getSize()).isPresent()) {
                return false;
            }

            // Check if all toppings exist
            if (cartItem.getToppingIds() != null && !cartItem.getToppingIds().isEmpty()) {
                List<Topping> existingToppings = toppingRepository.findAllById(cartItem.getToppingIds());
                if (existingToppings.size() != cartItem.getToppingIds().size()) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            // If any error occurs during validation, consider the item invalid
            return false;
        }
    }

    /**
     * Gets count of invalid items that were removed from cart
     */
    public int getInvalidItemsCount(List<CartItemDto> originalItems, List<CartItemDto> validItems) {
        return originalItems.size() - validItems.size();
    }
}
