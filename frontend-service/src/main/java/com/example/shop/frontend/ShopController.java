package com.example.shop.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ShopController {

    private final BackendClient backend;
    private final Cart cart;

    public ShopController(BackendClient backend, Cart cart) {
        this.backend = backend;
        this.cart = cart;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", backend.listProducts());
        model.addAttribute("cartCount", cart.totalQuantity());
        return "home";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable String id, Model model) {
        Optional<Map<String, Object>> product = backend.getProduct(id);
        if (product.isEmpty()) {
            return "redirect:/";
        }
        model.addAttribute("product", product.get());
        model.addAttribute("cartCount", cart.totalQuantity());
        return "product";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam String productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes flash) {
        cart.add(productId, quantity);
        flash.addFlashAttribute("flash", "Added to cart");
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam String productId) {
        cart.remove(productId);
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        List<Map<String, Object>> rows = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<String, Integer> entry : cart.getItems().entrySet()) {
            Optional<Map<String, Object>> p = backend.getProduct(entry.getKey());
            if (p.isEmpty()) continue;
            Map<String, Object> row = new HashMap<>(p.get());
            int qty = entry.getValue();
            BigDecimal unit = new BigDecimal(p.get().get("price").toString());
            BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(qty));
            row.put("quantity", qty);
            row.put("lineTotal", lineTotal);
            rows.add(row);
            total = total.add(lineTotal);
        }
        model.addAttribute("rows", rows);
        model.addAttribute("total", total);
        model.addAttribute("users", backend.listUsers());
        model.addAttribute("cartCount", cart.totalQuantity());
        return "cart";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam String userId, Model model, RedirectAttributes flash) {
        if (cart.isEmpty()) {
            flash.addFlashAttribute("flash", "Cart is empty");
            return "redirect:/cart";
        }
        List<Map<String, Object>> items = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : cart.getItems().entrySet()) {
            items.add(Map.of("productId", entry.getKey(), "quantity", entry.getValue()));
        }
        Map<String, Object> request = Map.of("userId", userId, "items", items);
        try {
            Map<String, Object> order = backend.placeOrder(request);
            cart.clear();
            model.addAttribute("order", order);
            return "confirmation";
        } catch (Exception e) {
            flash.addFlashAttribute("flash", "Checkout failed: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) String userId, Model model) {
        model.addAttribute("users", backend.listUsers());
        if (userId != null && !userId.isBlank()) {
            model.addAttribute("orders", backend.listOrdersByUser(userId));
            model.addAttribute("selectedUserId", userId);
        }
        model.addAttribute("cartCount", cart.totalQuantity());
        return "orders";
    }
}
