package com.example.starbuckscashierclient.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Sender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void makeDrink() {
        rabbitTemplate.convertAndSend("drinkMaker", "A bottle of starbucks drink");
    }
}
