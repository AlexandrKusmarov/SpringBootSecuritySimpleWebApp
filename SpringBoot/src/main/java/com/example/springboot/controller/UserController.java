package com.example.springboot.controller;

import com.example.springboot.Service.UserService;
import com.example.springboot.domain.Role;
import com.example.springboot.domain.User;
import com.example.springboot.repositories.UserCrudRepository;
import com.example.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public String userList(Model model){
        model.addAttribute("users",userRepository.findAll());

        return "userList";
    }

    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());

        return "userEdit";
    }

    @PostMapping
    public String userSave(
        @RequestParam String username,
        @RequestParam Map<String, String> form,
        @RequestParam ("userId") User user
    ) {

    user.setUsername(username);

        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());

        user.getRoles().clear();

        for (String key:form.keySet()) {
            if(roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepository.save(user);
        return "redirect:/user";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        userService.delete(id);

        return "redirect:/user";
    }

}
