package by.inno.kufar.controller;

import by.inno.kufar.model.User;
import by.inno.kufar.model.dto.AdvertisementDto;
import by.inno.kufar.model.dto.UserProfileUpdateDto;
import by.inno.kufar.service.AdvertisementService;
import by.inno.kufar.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final AdvertisementService advertisementService;

    @Autowired
    public ProfileController(UserService userService,
                             AdvertisementService advertisementService) {
        this.userService = userService;
        this.advertisementService = advertisementService;
    }

    @GetMapping
    public String profile(@AuthenticationPrincipal User user, Model model) {
        if (user == null) {
            return "redirect:/auth/register";
        }
        User fresh = userService.findByUsername(user.getUsername());

        if (fresh == null) {
            org.springframework.security.core.context.SecurityContextHolder.clearContext();
            return "redirect:/auth/login?error=deleted";
        }

        List<AdvertisementDto> myAds = advertisementService.findBySeller(fresh.getId());

        model.addAttribute("user", user);
        model.addAttribute("advertisements", myAds);
        return "profile/profile";
    }

    @GetMapping("/edit")
    public String editForm(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/auth/login";
        User fresh = userService.findByUsername(user.getUsername());
        UserProfileUpdateDto dto = new UserProfileUpdateDto();
        dto.setFullName(fresh.getFullName());
        dto.setEmail(fresh.getEmail());
        dto.setPhoneNumber(fresh.getPhoneNumber());
        dto.setCity(fresh.getCity());
        model.addAttribute("profile", dto);
        return "profile/edit";
    }

    @PostMapping("/edit")
    public String edit(@AuthenticationPrincipal User user,
                       @Valid @ModelAttribute("profile") UserProfileUpdateDto dto,
                       BindingResult bindingResult,
                       RedirectAttributes ra) {
        if (user == null) return "redirect:/auth/register";
        if (bindingResult.hasErrors()) {
            return "profile/edit";
        }
        try {
            userService.updateProfile(user.getUsername(), dto);
            ra.addFlashAttribute("success", "Данные профиля обновлены");
            return "redirect:/profile";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("email", "email.taken", e.getMessage());
            return "profile/edit";
        }
    }
}
