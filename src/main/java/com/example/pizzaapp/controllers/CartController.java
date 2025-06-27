package com.example.pizzaapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @GetMapping("/cart")
    public String cart(Model model) {
        return "pages/cart/index";
    }
}

// package com.example.pizzaapp.controllers;
// package com.example.pizzaapp.controller;

// import com.example.ex5.model.*;
// import com.example.ex5.repositories.*;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.*;

// import java.util.ArrayList;
// import java.util.List;

// /**
// * Controller for handling cart-related requests.
// */
// @Controller
// @RequestMapping("/cart")
// @SessionAttributes("cart")
// public class CartController {

// @Autowired
// private IngredientRepository ingredientRepository;

// @Autowired
// private PizzaRepository pizzaRepository;

// /**
// * Initializes a new cart.
// *
// * @return a new Cart object
// */
// @ModelAttribute("cart")
// public Cart cart() {
// return new Cart();
// }

// /**
// * Adds a pizza to the cart.
// *
// * @param ingredientIds the IDs of the ingredients for the pizza
// * @param cart the current cart
// * @param model the model to hold data for the view
// * @return the order view or redirect to the cart view
// */
// @PostMapping("/add")
// public String addToCart(@RequestParam(value = "ingredientIds", required =
// false) List<Long> ingredientIds,
// @ModelAttribute Cart cart, Model model) {
// if (ingredientIds == null || ingredientIds.size() < 2) {
// model.addAttribute("error", "You must select at least two ingredients.");
// model.addAttribute("ingredients", ingredientRepository.findAll()); // Ensure
// ingredients are passed back
// return "order/order";
// }

// Pizza pizza = new Pizza();
// pizza.setIngredients(ingredientRepository.findAllById(ingredientIds));
// pizza.calculatePrice();
// pizzaRepository.save(pizza);
// cart.addPizza(pizza);
// return "redirect:/cart";
// }

// /**
// * Displays the cart.
// *
// * @param cart the current cart
// * @param model the model to hold the cart
// * @return the cart view
// */
// @GetMapping
// public String viewCart(@ModelAttribute("cart") Cart cart, Model model) {
// model.addAttribute("cart", cart);
// return "cart";
// }

// /**
// * Displays the edit pizza form.
// *
// * @param id the ID of the pizza to edit
// * @param cart the current cart
// * @param model the model to hold data for the view
// * @return the edit pizza view or redirect to the cart view
// */
// @GetMapping("/edit/{id}")
// public String editPizza(@PathVariable("id") Long id, @ModelAttribute("cart")
// Cart cart, Model model) {
// Pizza pizza = cart.getPizzas().stream().filter(p ->
// p.getId().equals(id)).findFirst().orElse(null);
// if (pizza != null) {
// if (pizza.getIngredients() == null) {
// pizza.setIngredients(new ArrayList<>());
// }
// model.addAttribute("pizza", pizza);
// model.addAttribute("ingredients", ingredientRepository.findAll());
// return "order/editPizza";
// }
// return "redirect:/cart";
// }

// /**
// * Updates a pizza in the cart.
// *
// * @param ingredientIds the IDs of the ingredients for the pizza
// * @param pizza the pizza to update
// * @param cart the current cart
// * @param model the model to hold data for the view
// * @return the edit pizza view or redirect to the cart view
// */
// @PostMapping("/update")
// public String updatePizza(@RequestParam(value = "ingredientIds", required =
// false) List<Long> ingredientIds,
// @ModelAttribute("pizza") Pizza pizza, @ModelAttribute("cart") Cart cart,
// Model model) {
// if (ingredientIds == null || ingredientIds.size() < 2) {
// model.addAttribute("error", "You must select at least two ingredients.");
// model.addAttribute("ingredients", ingredientRepository.findAll());
// model.addAttribute("pizza", pizza); // Ensure the pizza object is passed back
// to the model
// return "order/editPizza";
// }

// pizza.setIngredients(ingredientRepository.findAllById(ingredientIds));
// pizza.calculatePrice();
// cart.updatePizza(pizza.getId(), pizza);
// return "redirect:/cart";
// }

// /**
// * Deletes a pizza from the cart.
// *
// * @param id the ID of the pizza to delete
// * @param cart the current cart
// * @return redirect to the cart view
// */
// @GetMapping("/delete/{id}")
// public String deletePizza(@PathVariable("id") Long id,
// @ModelAttribute("cart") Cart cart) {
// cart.getPizzas().removeIf(p -> p.getId().equals(id));
// return "redirect:/cart";
// }

// /**
// * Displays the checkout view.
// *
// * @param cart the current cart
// * @return the order details view or redirect to the cart view with an error
// */
// @GetMapping("/checkout")
// public String checkout(@ModelAttribute("cart") Cart cart) {
// if (cart.getPizzas().isEmpty() || cart.getTotalPrice() == 0) {
// return "redirect:/cart?error=empty";
// }
// return "order/orderDetails";
// }
// }
