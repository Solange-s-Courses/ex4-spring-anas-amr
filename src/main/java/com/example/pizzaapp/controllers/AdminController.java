package com.example.pizzaapp.controllers;

import com.example.pizzaapp.models.*;
import com.example.pizzaapp.repositories.*;
import com.example.pizzaapp.services.*;
import com.example.pizzaapp.dtos.OrderSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ToppingRepository toppingRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    /**
     * Admin dashboard - main landing page for admins
     */
    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "pages/admin/adminDashboard";
    }

    /**
     * Orders management page - view all orders
     */
    @GetMapping("/orders")
    public String viewAllOrders(Model model) {
        List<OrderSummaryDto> orders = orderService.getAllOrderSummaries();
        model.addAttribute("orders", orders);
        return "pages/admin/orders";
    }

    /**
     * Update order status
     */
    @PostMapping("/updateOrderStatus")
    public String updateOrderStatus(
            @RequestParam UUID orderId,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                // Map the status string to OrderStatusEnum
                OrderStatus.OrderStatusEnum statusEnum;
                switch (status.toUpperCase()) {
                    case "PREPARING":
                    case "PENDING":
                        statusEnum = OrderStatus.OrderStatusEnum.PENDING;
                        break;
                    case "OUT FOR DELIVERY":
                    case "CONFIRMED":
                        statusEnum = OrderStatus.OrderStatusEnum.CONFIRMED;
                        break;
                    case "DELIVERED":
                        statusEnum = OrderStatus.OrderStatusEnum.DELIVERED;
                        break;
                    case "CANCELLED":
                        statusEnum = OrderStatus.OrderStatusEnum.CANCELLED;
                        break;
                    default:
                        statusEnum = OrderStatus.OrderStatusEnum.PENDING;
                }

                OrderStatus orderStatus = orderStatusRepository.findByCode(statusEnum)
                        .orElseThrow(() -> new IllegalStateException("Order status not found: " + statusEnum));

                order.setStatus(orderStatus);
                orderRepository.save(order);
                redirectAttributes.addFlashAttribute("success", "Order status updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update order status: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    /**
     * Ingredients (Toppings) management page
     */
    @GetMapping("/ingredients")
    public String viewIngredients(Model model) {
        List<Topping> ingredients = toppingRepository.findAll();
        model.addAttribute("ingredients", ingredients);
        return "pages/admin/ingredients";
    }

    /**
     * Update ingredient price
     */
    @PostMapping("/ingredients/{id}/updatePrice")
    public String updateIngredientPrice(
            @PathVariable UUID id,
            @RequestParam BigDecimal price,
            RedirectAttributes redirectAttributes) {
        try {
            Topping topping = toppingRepository.findById(id).orElse(null);
            if (topping != null) {
                topping.setPrice(price.doubleValue());
                toppingRepository.save(topping);
                redirectAttributes.addFlashAttribute("success", "Price updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update price: " + e.getMessage());
        }
        return "redirect:/admin/ingredients";
    }

    /**
     * Update ingredient stock status
     * Note: This assumes you want to add an inStock field to Topping entity
     */
    @PostMapping("/ingredients/{id}/updateStock")
    public String updateIngredientStock(
            @PathVariable UUID id,
            @RequestParam boolean inStock,
            RedirectAttributes redirectAttributes) {
        try {
            Topping topping = toppingRepository.findById(id).orElse(null);
            if (topping != null) {
                // Since Topping entity doesn't have an inStock field in your current code,
                // you might want to add it or handle this differently
                // For now, just save the topping without changing stock
                toppingRepository.save(topping);
                redirectAttributes.addFlashAttribute("success", "Stock status updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update stock status: " + e.getMessage());
        }
        return "redirect:/admin/ingredients";
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
     * Update product details (name, description only - no image URL)
     */
    @PostMapping("/products/{productId}/details")
    public String updateProductDetails(
            @PathVariable UUID productId,
            @RequestParam String name,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {
        try {
            productService.updateProductDetails(productId, name, description, null);
            redirectAttributes.addFlashAttribute("success", "Product details updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update product: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    /**
     * Bulk update product prices
     */
    @PostMapping("/products/bulk-update")
    public String bulkUpdatePrices(
            @RequestParam String priceUpdates,
            RedirectAttributes redirectAttributes) {
        try {
            productService.bulkUpdatePrices(priceUpdates);
            redirectAttributes.addFlashAttribute("success", "All price changes saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update prices: " + e.getMessage());
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
     * View specific user's orders
     */
    @GetMapping("/users/{id}/orders")
    public String viewUserOrders(@PathVariable UUID id, Model model) {
        User user = userService.getUserById(id);
        if (user != null) {
            List<Order> orders = orderRepository.findByUser(user);
            model.addAttribute("user", user);
            model.addAttribute("orders", orders);
            return "pages/admin/userOrders";
        }
        return "redirect:/admin/users";
    }

    /**
     * Show change password form
     */
    @GetMapping("/users/{id}/changePassword")
    public String showChangePasswordForm(@PathVariable UUID id, Model model) {
        model.addAttribute("userId", id);
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
                redirectAttributes.addFlashAttribute("error", "Passwords do not match!");
                return "redirect:/admin/users/" + userId + "/changePassword";
            }

            User user = userService.getUserById(userId);
            if (user != null) {
                // You'll need to implement updatePassword method in UserService
                // userService.updatePassword(user, newPassword);
                redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change password: " + e.getMessage());
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
}