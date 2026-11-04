package by.inno.kufar.controller;

import by.inno.kufar.model.User;
import by.inno.kufar.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String cart(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("items", cartService.getItems(user));
        model.addAttribute("total", cartService.getTotal(user));
        return "cart";
    }

    @PostMapping("/add/{adId}")
    public String add(@PathVariable Long adId,
                      @AuthenticationPrincipal User user,
                      @RequestHeader(value = "Referer", required = false) String referer,
                      RedirectAttributes ra) {
        if (user == null) return "redirect:/auth/login";
        cartService.add(user, adId);
        ra.addFlashAttribute("success", "Товар добавлен в корзину");
        return "redirect:" + (referer != null ? referer : "/");
    }

    @PostMapping("/remove/{itemId}")
    public String remove(@PathVariable Long itemId,
                         @AuthenticationPrincipal User user) {
        if (user == null) return "redirect:/auth/login";
        cartService.remove(user, itemId);
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clear(@AuthenticationPrincipal User user) {
        if (user == null) return "redirect:/auth/login";
        cartService.clear(user);
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam("method") String method,
                           @AuthenticationPrincipal User user) {
        if (user == null) return "redirect:/auth/login";
        if ("card".equals(method)) {
            return "redirect:/payment/card";
        }
        // Оплата при получении — очищаем корзину и показываем успех
        cartService.clear(user);
        return "redirect:/cart?delivery=ok";
    }
}
