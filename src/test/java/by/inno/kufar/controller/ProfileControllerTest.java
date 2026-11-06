package by.inno.kufar.controller;

import by.inno.kufar.model.User;
import by.inno.kufar.service.AdvertisementService;
import by.inno.kufar.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AdvertisementService advertisementService;

    @Test
    void profile_ShouldRedirectToRegister_WhenUserIsNull() throws Exception {
        mockMvc.perform(get("/profile").with(anonymous()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error=deleted"));
    }

    @Test
    void profile_ShouldReturnProfileView() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setFullName("Test User");

        when(userService.findByUsername(any())).thenReturn(mockUser);
        when(advertisementService.findBySeller(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/profile").with(user(mockUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("profile/profile"))
                .andExpect(model().attributeExists("user", "advertisements"));
    }

    @Test
    void editProfile_ShouldRedirect_WhenSuccessful() throws Exception {
        User mockUser = new User();
        mockUser.setUsername("testuser");

        mockMvc.perform(post("/profile/edit")
                        .with(csrf())
                        .with(user(mockUser))
                        .param("fullName", "New Name")
                        .param("email", "new@mail.com")
                        .param("phoneNumber", "+375291234567")
                        .param("city", "Minsk"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(userService).updateProfile(any(), any());
    }
}