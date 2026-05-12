package com.hotel.strategy;

public interface PricingStrategy {
    void apply(PriceContext context);
    int getOrder();
}
