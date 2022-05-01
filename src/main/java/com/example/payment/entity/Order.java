package com.example.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Order {
    private double price = 50;
    private String currency = "USD";
    private String method;
    private String intent = "sale";
    private String description = "Payment";
}
