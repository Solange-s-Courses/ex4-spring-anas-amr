package com.example.pizzaapp.controllers;

import com.example.pizzaapp.models.Product;
import com.example.pizzaapp.models.User;
import com.example.pizzaapp.services.ProductService;
import com.example.pizzaapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final UserService userService;

    /**
     * Admin dashboard - main landing page for admins
     */
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "pages/admin/adminDashboard";
    }

    /**
     * Product management page - view all products with prices
     */
    @GetMapping("/products")
    public String manageProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "pages/admin/products";
    }

    /**
     * Update product price for a specific size
     */
    @PostMapping("/products/{productId}/options/{optionId}/price")
    public String updateProductPrice(
            @PathVariable UUID productId,
            @PathVariable UUID optionId,
            @RequestParam Double price,
            RedirectAttributes redirectAttributes) {
        try {
            productService.updateProductOptionPrice(optionId, price);
            redirectAttributes.addFlashAttribute("success", "Price updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update price: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    /**
     * Update product details (name, description, image)
     */
    @PostMapping("/products/{productId}/details")
    public String updateProductDetails(
            @PathVariable UUID productId,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) String imageUrl,
            RedirectAttributes redirectAttributes) {
        try {
            productService.updateProductDetails(productId, name, description, imageUrl);
            redirectAttributes.addFlashAttribute("success", "Product details updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    /**
     * Enhanced user management page
     */
    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "pages/admin/users";
    }

    /**
     * Update user role
     */
    @PostMapping("/users/{userId}/role")
    public String updateUserRole(
            @PathVariable UUID userId,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserRole(userId, role);
            redirectAttributes.addFlashAttribute("success", "User role updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user role: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * Toggle user account status (enable/disable)
     */
    @PostMapping("/users/{userId}/status")
    public String toggleUserStatus(
            @PathVariable UUID userId,
            RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(userId);
            redirectAttributes.addFlashAttribute("success", "User status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * Get product details for editing (AJAX endpoint)
     */
    @GetMapping("/api/products/{productId}")
    @ResponseBody
    public Product getProductDetails(@PathVariable UUID productId) {
        return productService.getProductById(productId);
    }
}
