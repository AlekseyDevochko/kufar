package by.inno.kufar.controller;

import by.inno.kufar.model.dto.UserRegistrationDto;
import by.inno.kufar.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    void register_ShouldReturnFormWithErrors_WhenPasswordsDoNotMatch() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .param("username", "test")
                        .param("password", "pass1")
                        .param("confirmPassword", "pass2"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors("user", "confirmPassword"));

        verify(userService, never()).register(any());
    }

    @Test
    void register_ShouldRegisterAndRedirect_WhenValid() throws Exception {
        when(userService.existsByUsername("newuser")).thenReturn(true);
        when(userService.existsByEmail("new@mail.com")).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("password", "password")
                        .param("confirmPassword", "password")
                        .param("email", "new@mail.com")
                        .param("fullName", "Full Name")
                        .param("phoneNumber", "+375291234567")
                        .param("city", "Minsk"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(flash().attributeExists("success"));

        verify(userService).register(any(UserRegistrationDto.class));
    }

    @Test
    void login_ShouldReturnLoginView_WhenBadCredentials() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad creds"));

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .param("username", "test")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("loginError"));
    }

    @Test
    void login_ShouldRedirectToHome_WhenSuccessful() throws Exception {
        Authentication authResult = mock(Authentication.class);
        when(authResult.getName()).thenReturn("testuser");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authResult);

        mockMvc.perform(post("/auth/login")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("password", "correct"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("success"));
    }
}