package com.example.starbuckscashierclient.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegisterCommand {
    private String action;
    private String store;
    private String drink;
    private String milk;
    private String size;
}
