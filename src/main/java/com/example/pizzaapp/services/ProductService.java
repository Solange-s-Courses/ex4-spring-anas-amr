package com.example.pizzaapp.services;

import com.example.pizzaapp.models.Product;
import com.example.pizzaapp.models.ProductOption;
import com.example.pizzaapp.repositories.ProductRepository;
import com.example.pizzaapp.repositories.ProductOptionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductService(ProductRepository productRepository, ProductOptionRepository productOptionRepository) {
        this.productRepository = productRepository;
        this.productOptionRepository = productOptionRepository;
    }

    // Method to find a product by its ID
    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    // Admin methods for product management

    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Get product by ID (throws exception if not found)
     */
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    /**
     * Update product option price
     */
    public void updateProductOptionPrice(UUID optionId, Double price) {
        ProductOption option = productOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Product option not found with id: " + optionId));

        option.setPrice(price);
        productOptionRepository.save(option);
    }

    /**
     * Update product details (name, description, image)
     */
    public void updateProductDetails(UUID productId, String name, String description, String imageUrl) {
        Product product = getProductById(productId);

        if (name != null && !name.trim().isEmpty()) {
            product.setName(name.trim());
        }

        if (description != null && !description.trim().isEmpty()) {
            product.setDescription(description.trim());
        }

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            product.setImageUrl(imageUrl.trim());
        }

        productRepository.save(product);
    }

    /**
     * Bulk update product prices
     */
    @Transactional
    public void bulkUpdatePrices(String priceUpdatesJson) {
        try {
            // Parse the JSON string to a Map
            Map<String, Double> priceUpdates = objectMapper.readValue(
                    priceUpdatesJson,
                    new TypeReference<Map<String, Double>>() {
                    });

            // Update each price
            for (Map.Entry<String, Double> entry : priceUpdates.entrySet()) {
                UUID optionId = UUID.fromString(entry.getKey());
                Double newPrice = entry.getValue();

                ProductOption option = productOptionRepository.findById(optionId)
                        .orElseThrow(() -> new RuntimeException("Product option not found with id: " + optionId));

                option.setPrice(newPrice);
                productOptionRepository.save(option);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse price updates: " + e.getMessage(), e);
        }
    }

    /**
     * Get product option by ID
     */
    public ProductOption getProductOptionById(UUID optionId) {
        return productOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Product option not found with id: " + optionId));
    }
}