package ru.itmentor.spring.boot_security.demo.controler;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.repositories.RoleRepository;
import ru.itmentor.spring.boot_security.demo.repositories.UserRepository;


import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin/users")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public String getAllUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String getUser(@PathVariable int id, Model model) {
        User user = userRepository.getReferenceById(id);
        model.addAttribute("user", Objects.requireNonNullElseGet(user, User::new));
        return "userPage";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String pageForUpdateUser(@PathVariable int id, Model model) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        List<Role> allRoles = roleRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);
        return "edit";
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, @PathVariable int id, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) { return "edit"; }
        User userForUpdate = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        Set<Role> attachedRoles = user.getRoles().stream()
                .map(role -> roleRepository.findById(role.getId())
                        .orElseThrow(() -> new RuntimeException("Role not found")))
                .collect(Collectors.toSet());
        userForUpdate.update(user, attachedRoles);
        userRepository.save(userForUpdate);
        attributes.addAttribute("id", user.getId()).addFlashAttribute("message", "user upgraded");
        return "redirect:/admin/users";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable int id, RedirectAttributes attributes) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        userRepository.delete(user);
        attributes.addAttribute("id", id).addFlashAttribute("message", "user deleted");
        return "redirect:/admin/users";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "new";
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) { return "new"; }
        userRepository.save(user);
        attributes.addAttribute("id", user.getId()).addFlashAttribute("message", "user added");
        return "redirect:/admin/users";
    }
}
