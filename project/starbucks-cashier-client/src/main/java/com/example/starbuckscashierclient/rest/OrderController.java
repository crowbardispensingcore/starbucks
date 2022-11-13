package com.example.starbuckscashierclient.rest;

import com.example.starbuckscashierclient.model.NewOrderRequest;
import com.example.starbuckscashierclient.model.OrderResponse;
import com.example.starbuckscashierclient.model.RegisterCommand;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j2
@Controller
public class OrderController {

//    private static final Map<String, String> stores = Map.of(
//            "5012349", "Dub-C",
//            "1287612", "P-Town",
//
//    );
    private static final List<String> DRINK_OPTIONS = new ArrayList<>(Arrays.asList(
            "Caffe Latte", "Caffe Americano", "Caffe Mocha",  "Cappuccino"
    ));

    private static final List<String> MILK_OPTIONS = new ArrayList<>(Arrays.asList(
            "Whole Milk", "2% Milk", "Nonfat Milk", "Almond Milk", "Soy Milk"
    ));

    private static final List<String> SIZE_OPTIONS = new ArrayList<>(Arrays.asList(
            "Tall", "Grande", "Venti", "Your Own Cup"
    ));

    private static final String hostEndpoint = "http://34.171.154.111/api";
    private static final String apiKey = "Zkfokey2311";

    @GetMapping("/")
    public String getPage(Model model) {
        String registerDisplay = String.format(
                "Starbucks Reserved Order\n\n\n" +
                        "Register: %s\n" +
                        "Status: Ready for New Order",
                "5012349"
        );


        model.addAttribute("registerDisplay", registerDisplay);
        model.addAttribute("drinks", DRINK_OPTIONS);
        model.addAttribute("milks", MILK_OPTIONS);
        model.addAttribute("sizes", SIZE_OPTIONS);

        return "cashier";
    }

    @PostMapping("/")
    public String postAction(
            @ModelAttribute("command") RegisterCommand command,
            @RequestParam(value = "action", required = true) String action,
            Errors errors, Model model, HttpServletRequest request
    ) {
        log.info(command);
        log.info("?action={}", action);

        String registerDisplay = "";
        String resourceUrl = "";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("apiKey", apiKey);

        if (action.equals("Clear Order")) {
            log.info("Performing Clear Order action");
            resourceUrl = hostEndpoint + "/order/register/" + command.getStore();
            log.info("Resource URL: {}", resourceUrl);
            ResponseEntity<String> response;
            HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
            try {
                response = restTemplate.exchange(resourceUrl, HttpMethod.DELETE, httpEntity, String.class);
                log.info(response.getBody());
                registerDisplay = String.format(
                        "Starbucks Reserved Order\n\n\nRegister: %s\nStatus: Active Order Cleared",
                        command.getStore()
                );
            }
            catch (Exception e) {
                log.info(e.getMessage());
                registerDisplay = String.format(
                        "Starbucks Reserved Order\n\n\nRegister: %s\nStatus: Unable to clear order. Order Not Found!",
                        command.getStore()
                );
            }
        }
        else if (action.equals("Place Order")) {
            log.info("Performing Place Order action");
            resourceUrl = hostEndpoint + "/order/register/" + command.getStore();
            log.info("Resource URL: {}", resourceUrl);
            NewOrderRequest newOrderRequest = new NewOrderRequest(command.getDrink(), command.getMilk(), command.getSize());
            ResponseEntity<OrderResponse> newOrderResponse;
            HttpEntity<NewOrderRequest> httpEntity = new HttpEntity<>(newOrderRequest, httpHeaders);
            try {
                newOrderResponse = restTemplate.exchange(resourceUrl, HttpMethod.POST, httpEntity, OrderResponse.class);
                log.info(newOrderResponse.getBody());
                OrderResponse newOrder = newOrderResponse.getBody();
                registerDisplay = String.format(
                        "Starbucks Reserved Order\n\n\n" +
                                "Drink: %s\n" +
                                "Milk: %s\n" +
                                "Size: %s\n" +
                                "Total: %f\n" +
                                "Register: %s\n" +
                                "Status: Order placed. Ready for payment",
                        newOrder.getDrink(), newOrder.getMilk(), newOrder.getSize(),
                        newOrder.getTotal(), newOrder.getRegister()
                );
            }
            catch (Exception e) {
                log.info(e.getMessage());
                registerDisplay = String.format(
                        "Starbucks Reserved Order\n\n\nRegister: %s\nStatus: Unable to place order. Active Order Exists!",
                        command.getStore()
                );
            }
        }

        model.addAttribute("registerDisplay", registerDisplay);
        model.addAttribute("drinks", DRINK_OPTIONS);
        model.addAttribute("milks", MILK_OPTIONS);
        model.addAttribute("sizes", SIZE_OPTIONS);

        return "cashier";
    }
}
