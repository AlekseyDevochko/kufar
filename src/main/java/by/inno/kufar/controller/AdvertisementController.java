package by.inno.kufar.controller;

import by.inno.kufar.model.User;
import by.inno.kufar.model.dto.AdvertisementCreateDto;
import by.inno.kufar.service.AdvertisementService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ads")
public class AdvertisementController {

    private final AdvertisementService service;

    public AdvertisementController(AdvertisementService service) {
        this.service = service;
    }

    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/auth/register";
        if (!model.containsAttribute("ad")) {
            model.addAttribute("ad", new AdvertisementCreateDto());
        }
        model.addAttribute("mode", "create");
        model.addAttribute("user", user);
        return "ads/form";
    }

    @PostMapping("/new")
    public String create(@AuthenticationPrincipal User user,
                         @Valid @ModelAttribute("ad") AdvertisementCreateDto dto,
                         BindingResult br,
                         Model model,
                         RedirectAttributes ra) {
        if (user == null) return "redirect:/auth/register";
        if (br.hasErrors()) {
            model.addAttribute("mode", "create");
            model.addAttribute("user", user);
            return "ads/form";
        }
        service.create(dto, user);
        ra.addFlashAttribute("success", "Объявление опубликовано");
        return "redirect:/profile";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal User user,
                           Model model) {
        if (user == null) return "redirect:/auth/register";
        var ad = service.findById(id);
        if (!user.getUsername().equals(ad.getSellerUsername())) {
            return "redirect:/";
        }
        AdvertisementCreateDto dto = new AdvertisementCreateDto();
        dto.setTitle(ad.getTitle());
        dto.setDescription(ad.getDescription());
        dto.setPrice(ad.getPrice());
        dto.setCity(ad.getCity());

        model.addAttribute("ad", dto);
        model.addAttribute("existingImages", ad.getImages());
        model.addAttribute("mode", "edit");
        model.addAttribute("adId", id);
        model.addAttribute("user", user);
        return "ads/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @AuthenticationPrincipal User user,
                       @Valid @ModelAttribute("ad") AdvertisementCreateDto dto,
                       BindingResult br,
                       Model model,
                       RedirectAttributes ra) {
        if (user == null) return "redirect:/auth/register";
        if (br.hasErrors()) {
            var ad = service.findById(id);
            model.addAttribute("existingImages", ad.getImages());
            model.addAttribute("mode", "edit");
            model.addAttribute("adId", id);
            model.addAttribute("user", user);
            return "ads/form";
        }
        try {
            service.update(id, dto, user);
            ra.addFlashAttribute("success", "Объявление обновлено");
            return "redirect:/ads/" + id;
        } catch (SecurityException e) {
            return "redirect:/";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal User user,
                         RedirectAttributes ra) {
        if (user == null) return "redirect:/auth/register";
        try {
            service.delete(id, user);
            ra.addFlashAttribute("success", "Объявление удалено");
        } catch (SecurityException ignored) {}
        return "redirect:/profile";
    }
}