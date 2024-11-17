package com.racha.restdev.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController 
@CrossOrigin(origins = "https://photovault011.onrender.com", maxAge = 3600)
public class HomeController {
    @GetMapping("/api/v1")
    public String home() {
        return "hello world";
    }
}
