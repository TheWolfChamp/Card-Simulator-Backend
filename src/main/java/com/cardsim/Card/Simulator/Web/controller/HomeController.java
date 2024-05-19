package com.cardsim.Card.Simulator.Web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Controller
public class HomeController {
    @RequestMapping("/")
    public String home() {
        return "Hello, Home!";
    }

    @RequestMapping("/secured")
    public String secured() {
        return "Hello, Secured!";
    }

}