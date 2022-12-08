package com.example.starbuckscashierclient.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class Receiver {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String takeDrink() {
        return (String) rabbitTemplate.receiveAndConvert("drinkMaker");
    }

}
