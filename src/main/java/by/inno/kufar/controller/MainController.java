package by.inno.kufar.controller;

import by.inno.kufar.model.User; // 👈 Импортируем ваш класс User
import by.inno.kufar.model.dto.AdvertisementDto;
import by.inno.kufar.model.dto.AdvertisementFilterDto;
import by.inno.kufar.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    private AdvertisementService advertisementService;

    @Autowired
    public MainController(AdvertisementService advertisementService) {
        this.advertisementService = advertisementService;
    }

    @GetMapping("/")
    public String index(@ModelAttribute("filter") AdvertisementFilterDto filter,
                        @RequestParam(value = "view", defaultValue = "grid") String view,
                        @AuthenticationPrincipal User currentUser,
                        Model model) {

        Page<AdvertisementDto> page = advertisementService.search(filter);

        model.addAttribute("page", page);
        model.addAttribute("cities", advertisementService.findAllCities());
        model.addAttribute("view", "list".equals(view) ? "list" : "grid");
        model.addAttribute("user", currentUser);
        return "main";
    }

    @GetMapping("/ads/{id}")
    public String advertisementDetail(@PathVariable Long id,
                                      Model model,
                                      @AuthenticationPrincipal User currentUser) {
        AdvertisementDto ad = advertisementService.findById(id);
        model.addAttribute("ad", ad);

        boolean isAuthenticated = currentUser != null && currentUser.getUsername() != null;

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("currentUsername", isAuthenticated ? currentUser.getUsername() : null);
        model.addAttribute("user", currentUser);
        return "ads/detail";
    }
}