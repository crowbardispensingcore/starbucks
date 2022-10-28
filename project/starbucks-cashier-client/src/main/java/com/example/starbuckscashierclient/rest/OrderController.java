package com.example.starbuckscashierclient.rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderController {

    @GetMapping("/")
    public String getPage(Model model) {
        String statusDisplay = "Starbucks Reserved Order\n\n\nRegister: 1\nStatus: Ready for New Order";

        model.addAttribute("statusDisplay", statusDisplay);

        return "cashier";
    }
}
