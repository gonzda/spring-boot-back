package com.backend_spring.spring_back_test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api")
    public String api() {
        return "Hello fdsa World";
    }
}
