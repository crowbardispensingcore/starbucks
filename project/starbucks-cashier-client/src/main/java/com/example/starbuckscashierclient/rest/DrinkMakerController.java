package com.example.starbuckscashierclient.rest;

import com.example.starbuckscashierclient.messaging.Receiver;
import com.example.starbuckscashierclient.messaging.Sender;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Log4j2
@RestController
public class DrinkMakerController {
    @Autowired
    private Receiver receiver;
    @Autowired
    private Sender sender;

    @GetMapping("/drink-maker")
    String takeDrink(HttpServletResponse response) {
        String drink = receiver.takeDrink();
        if (drink == null || drink.equals("")) {
            return "No drink is available.";
        } else {
            return "You get a Bottle of Starbucks Drink!" + " What you get: '" + drink + "'";
        }
    }

    @PostMapping("/drink-maker")
    String makeDrink(HttpServletResponse response) {
        sender.makeDrink();

        return "A new order has been sent to the drink maker.";
    }
}
