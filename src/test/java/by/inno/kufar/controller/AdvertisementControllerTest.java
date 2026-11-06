package by.inno.kufar.controller;

import by.inno.kufar.model.User;
import by.inno.kufar.service.AdvertisementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdvertisementController.class)
class AdvertisementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdvertisementService advertisementService;

    @Test
    void create_ShouldReturnFormWithErrors_WhenValidationFails() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        mockMvc.perform(post("/ads/new")
                        .with(csrf())
                        .with(user(mockUser))
                        .param("title", "")
                        .param("price", "-10"))
                .andExpect(status().isOk())
                .andExpect(view().name("ads/form"))
                .andExpect(model().attributeHasFieldErrors("ad", "title", "price"));

        verify(advertisementService, never()).create(any(), any());
    }

    @Test
    void create_ShouldRedirect_WhenValid() throws Exception {
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setUsername("testuser");

        mockMvc.perform(post("/ads/new")
                        .with(csrf())
                        .with(user(currentUser))
                        .param("title", "Valid Title")
                        .param("description", "Valid Description")
                        .param("price", "150.00")
                        .param("city", "Минск"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(advertisementService, times(1)).create(any(), any(User.class));
    }
}