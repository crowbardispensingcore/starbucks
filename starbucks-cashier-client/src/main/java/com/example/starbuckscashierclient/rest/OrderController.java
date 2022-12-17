package com.example.starbuckscashierclient.rest;

import com.example.starbuckscashierclient.model.NewOrderRequest;
import com.example.starbuckscashierclient.model.OrderResponse;
import com.example.starbuckscashierclient.model.RegisterCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

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

    @Value("${api.host.endpoint}")
    private String HOST_ENDPOINT;
    @Value("${api.key}")
    private String API_KEY;
    private static final ObjectMapper mapper = new ObjectMapper();

    @RequestMapping("/")
    public RedirectView redirectToRegister() {
        return new RedirectView("/register");
    }

    @GetMapping("/register")
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

        return "register";
    }

    @PostMapping("/register")
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
        httpHeaders.set("apiKey", API_KEY);

        if (action.equals("Clear Order")) {
            log.info("Performing Clear Order action");
            resourceUrl = "http://" + HOST_ENDPOINT + "/order/register/" + command.getStore();
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
            catch (HttpClientErrorException e) {
                log.info(e.getMessage());
                registerDisplay = String.format(
                        "Starbucks Reserved Order\n\n\n" +
                                "Register: %s\n" +
                                "Status: %s",
                        command.getStore(), parseResponseErrorMessage(e)
                );
            }
        }
        else if (action.equals("Place Order")) {
            log.info("Performing Place Order action");
            resourceUrl = "http://" + HOST_ENDPOINT + "/order/register/" + command.getStore();
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
            catch (HttpClientErrorException e) {
                log.info(e.getMessage());
                registerDisplay = String.format(
                        "Starbucks Reserved Order\n\n\n" +
                                "Register: %s\n" +
                                "Status: %s",
                        command.getStore(), parseResponseErrorMessage(e)
                );
            }
        }
        else if (action.equals("Refresh Order")) {
            log.info("Performing Refresh Order action");
            resourceUrl = "http://" + HOST_ENDPOINT + "/order/register/" + command.getStore();
            ResponseEntity<OrderResponse> orderResponse;
            HttpEntity<NewOrderRequest> httpEntity = new HttpEntity<>(httpHeaders);
            try {
                orderResponse = restTemplate.exchange(resourceUrl, HttpMethod.GET, httpEntity, OrderResponse.class);
                log.info(orderResponse.getBody());
                OrderResponse order = orderResponse.getBody();
                registerDisplay = String.format(
                        "Starbucks Reserved Order\n\n\n" +
                                "Drink: %s\n" +
                                "Milk: %s\n" +
                                "Size: %s\n" +
                                "Total: %f\n" +
                                "Register: %s\n" +
                                "Status: %s",
                        order.getDrink(), order.getMilk(), order.getSize(),
                        order.getTotal(), order.getRegister(), order.getStatus()
                );
            }
            catch (HttpClientErrorException e) {
                log.info(e.getMessage());
                registerDisplay = String.format(
                        "Starbucks Reserved Order\n\n\n" +
                                "Register: %s\n" +
                                "Status: %s",
                        command.getStore(), parseResponseErrorMessage(e)
                );
            }
        }

        model.addAttribute("registerDisplay", registerDisplay);
        model.addAttribute("drinks", DRINK_OPTIONS);
        model.addAttribute("milks", MILK_OPTIONS);
        model.addAttribute("sizes", SIZE_OPTIONS);

        return "register";
    }

    private String parseResponseErrorMessage(HttpClientErrorException e) {
        String responseString = e.getResponseBodyAsString();
        try {
            Map<String, Object> response = mapper.readValue(responseString, Map.class);
            return response.get("message").toString();
        }
        catch (Exception pe) {
            return "Unknown error!";
        }
    }
}
