package com.example.loginservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("/logined")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String test() throws IOException {

        return "Hello!";
    }
    @GetMapping("/basic")
    public String basic() throws IOException {

        return "Hello! basic";
    }
}
