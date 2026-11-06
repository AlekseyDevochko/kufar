package by.inno.kufar.controller;

import by.inno.kufar.model.User;
import by.inno.kufar.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    void cardForm_ShouldReturnViewWithTotalAmount() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("testuser");
        when(cartService.getTotal(any())).thenReturn(BigDecimal.valueOf(500));

        mockMvc.perform(get("/payment/card").with(user(mockUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("payment/card"))
                .andExpect(model().attributeExists("card", "user"));
    }

    @Test
    void pay_ShouldReturnViewWithErrors_WhenCardDataInvalid() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        mockMvc.perform(post("/payment/card")
                        .with(csrf())
                        .with(user(mockUser))
                        .param("cardNumber", "")
                        .param("cardHolder", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("payment/card"))
                .andExpect(model().attributeHasFieldErrors("card", "cardNumber", "cardHolder"));
    }
}