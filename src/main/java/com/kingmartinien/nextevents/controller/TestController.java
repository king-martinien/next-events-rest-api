package com.kingmartinien.nextevents.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping
    public String test() {
        return "test";
    }

    @GetMapping("provider")
    @PreAuthorize("hasAuthority('PROVIDER')")
    public String provider() {
        return "provider";
    }

}
