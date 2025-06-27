// package com.example.pizzaapp.controller;

// import com.example.ex5.model.*;
// import com.example.ex5.repositories.*;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import java.util.List;
// /**
//  * Controller for handling admin-related requests.
//  */
// @Controller
// @RequestMapping("/admin")
// public class AdminController {

//     @Autowired
//     private OrderRepository orderRepository;

//     @Autowired
//     private IngredientRepository ingredientRepository;

//     @Autowired
//     private UserRepository userRepository;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     /**
//      * Displays the admin dashboard.
//      * @return the admin dashboard view
//      */
//     @GetMapping("/dashboard")
//     public String adminDashboard() {
//         return "admin/adminDashboard";
//     }

//     /**
//      * Displays all orders.
//      * @param model the model to hold the orders
//      * @return the orders view
//      */
//     @GetMapping("/orders")
//     public String viewAllOrders(Model model) {
//         List<Order> orders = orderRepository.findAll();
//         model.addAttribute("orders", orders);
//         return "admin/orders";
//     }

//     /**
//      * Updates the status of an order.
//      * @param orderId the ID of the order to update
//      * @param status the new status of the order
//      * @return redirect to the orders view
//      */
//     @PostMapping("/updateOrderStatus")
//     public String updateOrderStatus(@RequestParam("orderId") Long orderId, @RequestParam("status") String status) {
//         Order order = orderRepository.findById(orderId).orElse(null);
//         if (order != null) {
//             order.setStatus(status);
//             orderRepository.save(order);
//         }
//         return "redirect:/admin/orders";
//     }

//     /**
//      * Displays all ingredients.
//      * @param model the model to hold the ingredients
//      * @return the ingredients view
//      */
//     @GetMapping("/ingredients")
//     public String viewIngredients(Model model) {
//         List<Ingredient> ingredients = ingredientRepository.findAll();
//         model.addAttribute("ingredients", ingredients);
//         return "admin/ingredients";
//     }

//     /**
//      * Updates the stock status of an ingredient.
//      * @param ingredientId the ID of the ingredient to update
//      * @param inStock the new stock status of the ingredient
//      * @return redirect to the ingredients view
//      */
//     @PostMapping("/ingredients/{ingredientId}/updateStock")
//     public String updateIngredientStock(@PathVariable Long ingredientId, @RequestParam boolean inStock) {
//         Ingredient ingredient = ingredientRepository.findById(ingredientId).orElse(null);
//         if (ingredient != null) {
//             ingredient.setInStock(inStock);
//             ingredientRepository.save(ingredient);
//         }
//         return "redirect:/admin/ingredients";
//     }

//     /**
//      * Updates the price of an ingredient.
//      * @param ingredientId the ID of the ingredient to update
//      * @param price the new price of the ingredient
//      * @return redirect to the ingredients view
//      */
//     @PostMapping("/ingredients/{ingredientId}/updatePrice")
//     public String updateIngredientPrice(@PathVariable Long ingredientId, @RequestParam double price) {
//         Ingredient ingredient = ingredientRepository.findById(ingredientId).orElse(null);
//         if (ingredient != null) {
//             ingredient.setPrice(price);
//             ingredientRepository.save(ingredient);
//         }
//         return "redirect:/admin/ingredients";
//     }

//     /**
//      * Displays all users.
//      * @param model the model to hold the users
//      * @return the users view
//      */
//     @GetMapping("/users")
//     public String viewUsers(Model model) {
//         List<User> users = userRepository.findAll();
//         model.addAttribute("users", users);
//         return "admin/users";
//     }

//     /**
//      * Displays the orders of a specific user.
//      * @param userId the ID of the user whose orders are to be displayed
//      * @param model the model to hold the user and orders
//      * @return the user orders view
//      */
//     @GetMapping("/users/{userId}/orders")
//     public String viewUserOrders(@PathVariable Long userId, Model model) {
//         User user = userRepository.findById(userId).orElse(null);
//         if (user != null) {
//             List<Order> orders = orderRepository.findByUserId(userId);
//             model.addAttribute("user", user);
//             model.addAttribute("orders", orders);
//         } else {
//             System.out.println("User not found with ID: " + userId);
//         }
//         return "admin/userOrders";
//     }

//     /**
//      * Displays the form for changing a user's password.
//      * @param userId the ID of the user whose password is to be changed
//      * @param model the model to hold the user ID
//      * @return the change password form view
//      */
//     @GetMapping("/users/{userId}/changePassword")
//     public String showChangePasswordForm(@PathVariable Long userId, Model model) {
//         User user = userRepository.findById(userId).orElse(null);
//         if (user != null) {
//             model.addAttribute("userId", userId);
//         } else {
//             model.addAttribute("error", "User not found.");
//         }
//         return "admin/changeUserPassword";
//     }

//     /**
//      * Changes a user's password.
//      * @param userId the ID of the user whose password is to be changed
//      * @param newPassword the new password
//      * @param confirmPassword the confirmation of the new password
//      * @param redirectAttributes attributes for redirecting with flash messages
//      * @return redirect to the users view
//      */
//     @PostMapping("/users/changePassword")
//     public String changeUserPassword(@RequestParam("userId") Long userId,
//                                      @RequestParam("newPassword") String newPassword,
//                                      @RequestParam("confirmPassword") String confirmPassword,
//                                      RedirectAttributes redirectAttributes) {
//         if (!newPassword.equals(confirmPassword)) {
//             redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
//             return "redirect:/admin/users/" + userId + "/changePassword";
//         }

//         User user = userRepository.findById(userId).orElse(null);
//         if (user != null) {
//             user.setPassword(passwordEncoder.encode(newPassword));
//             userRepository.save(user);
//             redirectAttributes.addFlashAttribute("success", "Password changed successfully.");
//         } else {
//             redirectAttributes.addFlashAttribute("error", "User not found.");
//         }
//         return "redirect:/admin/users";
//     }
// }
