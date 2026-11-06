package by.inno.kufar.controller;

import by.inno.kufar.model.User;
import by.inno.kufar.model.dto.AdvertisementDto;
import by.inno.kufar.service.AdvertisementService;
import by.inno.kufar.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AdvertisementService advertisementService;

    public UserController(
            UserService userService,
            AdvertisementService advertisementService
    ) {
        this.userService = userService;
        this.advertisementService = advertisementService;
    }

    @GetMapping("/{id}")
    public String sellerProfile(
            @PathVariable Long id,
            Model model
    ) {
        User seller = userService.findById(id);
        List<AdvertisementDto> sellerAds = advertisementService.findBySeller(id);

        model.addAttribute("seller", seller);
        model.addAttribute("advertisements", sellerAds);
        return "seller";
    }
}
