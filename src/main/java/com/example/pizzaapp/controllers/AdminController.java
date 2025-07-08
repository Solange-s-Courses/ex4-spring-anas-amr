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
     * Fix existing users - enable all users that might be disabled due to migration
     */
    @PostMapping("/users/unblockAll")
    public String unblockAllUsers(RedirectAttributes redirectAttributes) {
        try {
            userService.enableAllExistingUsers();
            redirectAttributes.addFlashAttribute("success", "All existing users have been unblocked successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to unblock users: " + e.getMessage());
        }
        return "redirect:/admin/users";
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

    /**
     * Block/Unblock a user
     */
    @PostMapping("/users/{userId}/block")
    public String toggleUserBlock(
            @PathVariable UUID userId,
            RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(userId);
            boolean wasBlocked = !user.isEnabled();

            userService.toggleUserAccess(userId);

            String action = wasBlocked ? "unblocked" : "blocked";
            redirectAttributes.addFlashAttribute("success", "User has been " + action + " successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user access: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /**
     * Show change password form for a user
     */
    @GetMapping("/users/{userId}/changePassword")
    public String showChangePasswordForm(@PathVariable UUID userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("userId", userId);
        return "pages/admin/changeUserPassword";
    }

    /**
     * Change user password
     */
    @PostMapping("/users/changePassword")
    public String changeUserPassword(
            @RequestParam UUID userId,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return "redirect:/admin/users/" + userId + "/changePassword";
            }

            if (newPassword.length() < 3) {
                redirectAttributes.addFlashAttribute("error", "Password must be at least 3 characters long");
                return "redirect:/admin/users/" + userId + "/changePassword";
            }

            userService.changeUserPassword(userId, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change password: " + e.getMessage());
            return "redirect:/admin/users/" + userId + "/changePassword";
        }
    }

    /**
     * View user orders
     */
    @GetMapping("/users/{userId}/orders")
    public String viewUserOrders(@PathVariable UUID userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        model.addAttribute("orders", user.getOrders());
        return "pages/admin/userOrders";
    }
}
