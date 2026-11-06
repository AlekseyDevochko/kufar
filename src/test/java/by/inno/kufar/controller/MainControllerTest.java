package by.inno.kufar.controller;

import by.inno.kufar.model.User;
import by.inno.kufar.model.dto.AdvertisementDto;
import by.inno.kufar.service.AdvertisementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MainController.class)
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdvertisementService advertisementService;

    @TestConfiguration
    static class SecurityTestConfig {
        @Bean
        public SecurityExpressionHandler<FilterInvocation> securityExpressionHandler() {
            return new DefaultWebSecurityExpressionHandler();
        }

        @Bean
        public AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver() {
            return new AuthenticationPrincipalArgumentResolver();
        }
    }

    @Test
    void index_ShouldReturnMainView() throws Exception {
        when(advertisementService.search(any())).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(advertisementService.findAllCities()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("page", "cities", "view", "user"));
    }

    @Test
    void advertisementDetail_ShouldReturnDetailView() throws Exception {
        AdvertisementDto ad = new AdvertisementDto();
        ad.setId(1L);
        ad.setTitle("Test Ad");
        ad.setPrice(BigDecimal.valueOf(1500));
        ad.setSellerName("Test Seller");
        ad.setSellerUsername("seller");
        ad.setDescription("Описание");
        ad.setCity("Минск");

        when(advertisementService.findById(1L)).thenReturn(ad);

        User mockUser = new User();
        mockUser.setUsername("testuser");

        mockMvc.perform(get("/ads/1").with(user(mockUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("ads/detail"))
                .andExpect(model().attributeExists("ad", "isAuthenticated", "user"));
    }
}