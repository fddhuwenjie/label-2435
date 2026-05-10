package com.hotel.util.price;

public interface PricingStrategy {

    void apply(PriceContext context);

    int getOrder();
}
