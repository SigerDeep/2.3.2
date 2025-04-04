package ru.itmentor.spring.boot_security.demo.controler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.User;


import java.util.Objects;


@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping("/profile")
    public String getUser(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails);
        return "user";
    }
}
