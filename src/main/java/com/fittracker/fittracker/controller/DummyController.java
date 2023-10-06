package com.fittracker.fittracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//TODO: just for testing purposes, to be removed
public class DummyController {

    @GetMapping("hello")
    public String hello() {
        return "Hello!";
    }
}
