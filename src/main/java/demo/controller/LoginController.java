package demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Spring security custom login page
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        // navigate to src/main/resources/templates/login.html
        return "login";
    }
}
