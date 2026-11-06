package by.inno.kufar.controller;

import by.inno.kufar.model.dto.LoginDto;
import by.inno.kufar.model.dto.UserRegistrationDto;
import by.inno.kufar.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private UserService userService;
    private AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(
            UserService userService,
            AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/register")
    public String showRegisterForm(
            Model model
    ) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("user") UserRegistrationDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch",
                    "Пароли не совпадают");
        }
        if (userService.existsByUsername(dto.getUsername())) {
            bindingResult.rejectValue("username", "username.exists",
                    "Пользователь с таким логином уже существует");
        }
        if (userService.existsByEmail(dto.getEmail())) {
            bindingResult.rejectValue("email", "email.exists",
                    "Пользователь с таким email уже существует");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        userService.register(dto);
        redirectAttributes.addFlashAttribute("success", "Регистрация успешна! Войдите в аккаунт.");
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String loginPage(
            Model model,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout
    ) {

        if (!model.containsAttribute("loginDto")) {
            model.addAttribute("loginDto", new LoginDto());
        }
        if (error != null) {
            model.addAttribute("loginError", "Неверный логин или пароль");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Вы успешно вышли из аккаунта");
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("loginDto") LoginDto dto,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        if (bindingResult.hasErrors()) {
            return "auth/login";
        }

        try {
            Authentication authRequest = new UsernamePasswordAuthenticationToken(
                    dto.getUsername(), dto.getPassword());
            Authentication authResult = authenticationManager.authenticate(authRequest);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authResult);
            SecurityContextHolder.setContext(context);

            request.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context);

            redirectAttributes.addFlashAttribute("success",
                    "Добро пожаловать, " + authResult.getName() + "!");
            return "redirect:/";

        } catch (BadCredentialsException e) {
            model.addAttribute("loginError", "Неверный логин или пароль");
            return "auth/login";

        } catch (AuthenticationException e) {
            model.addAttribute("loginError", "Ошибка авторизации. Попробуйте позже.");
            return "auth/login";
        }
    }
}
