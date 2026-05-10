package com.hotel.pricing;

public interface PricingStrategy {

    PricingContext apply(PricingContext context);
}
