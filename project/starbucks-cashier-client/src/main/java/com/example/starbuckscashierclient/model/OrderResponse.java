package com.example.starbuckscashierclient.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class OrderResponse {
    private String drink;
    private String milk;
    private String size;
    private Double total;
    private String status;
    private String register;
}
