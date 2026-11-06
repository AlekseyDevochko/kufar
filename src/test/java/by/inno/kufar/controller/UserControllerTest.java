package by.inno.kufar.controller;

import by.inno.kufar.model.User;
import by.inno.kufar.service.AdvertisementService;
import by.inno.kufar.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AdvertisementService advertisementService;

    @Test
    void sellerProfile_ShouldReturnSellerView() throws Exception {
        Long sellerId = 1L;
        User seller = new User();
        seller.setId(sellerId);

        when(userService.findById(sellerId)).thenReturn(seller);
        when(advertisementService.findBySeller(sellerId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/{id}", sellerId))
                .andExpect(status().isOk())
                .andExpect(view().name("seller"))
                .andExpect(model().attributeExists("seller", "advertisements"));
    }
}
