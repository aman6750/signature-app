package com.signatureapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Signature App is running! 🚀";
    }

    @GetMapping("/me")
    public String whoAmI(org.springframework.security.core.Authentication authentication) {
        if (authentication == null) {
            return "Not authenticated";
        }
        com.signatureapp.model.User user = (com.signatureapp.model.User) authentication.getPrincipal();
        return "Hello " + user.getName() + "! Your email is " + user.getEmail() + " and role is " + user.getRole();
    }
}
