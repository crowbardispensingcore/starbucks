package com.example.starbuckscashierclient.model;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class NewOrderRequest {
    @NonNull
    private String drink;
    @NonNull
    private String milk;
    @NonNull
    private String size;
}
