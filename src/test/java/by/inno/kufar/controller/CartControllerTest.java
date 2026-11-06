package by.inno.kufar.controller;

import by.inno.kufar.model.User;
import by.inno.kufar.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    void cart_ShouldReturnCartView_WhenAuthenticated() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        when(cartService.getItems(any())).thenReturn(Collections.emptyList());
        when(cartService.getTotal(any())).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(get("/cart").with(user(mockUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items", "total", "user"));
    }

    @Test
    void add_ShouldRedirectToReferer() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        mockMvc.perform(post("/cart/add/1")
                        .with(csrf())
                        .with(user(mockUser))
                        .header("Referer", "/ads/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ads/1"));

        verify(cartService).add(any(User.class), eq(1L));
    }

    @Test
    void checkout_ShouldRedirectToCard_WhenCardMethodSelected() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        mockMvc.perform(post("/cart/checkout")
                        .with(csrf())
                        .with(user(mockUser))
                        .param("method", "card"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/payment/card"));
    }
}