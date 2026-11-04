package by.inno.kufar.controller;

import by.inno.kufar.model.User;
import by.inno.kufar.model.dto.CardPaymentDto;
import by.inno.kufar.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final CartService cartService;

    public PaymentController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/card")
    public String cardForm(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/auth/login";
        if (!model.containsAttribute("card")) {
            CardPaymentDto dto = new CardPaymentDto();
            dto.setAmount(cartService.getTotal(user));
            model.addAttribute("card", dto);
        }
        return "payment/card";
    }

    /** Заглушка: ничего не делает, просто перерисовывает форму */
    @PostMapping("/card")
    public String pay(@AuthenticationPrincipal User user,
                      @Valid @ModelAttribute("card") CardPaymentDto dto,
                      BindingResult br,
                      Model model) {
        if (user == null) return "redirect:/auth/login";
        if (br.hasErrors()) {
            return "payment/card";
        }
        // TODO: интеграция с платёжным шлюзом — пока не реализована
        return "payment/card";
    }
}

