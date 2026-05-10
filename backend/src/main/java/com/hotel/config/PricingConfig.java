package com.hotel.config;

import com.hotel.service.SysHolidayService;
import com.hotel.util.PriceCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PricingConfig {

    @Bean
    public PriceCalculator priceCalculator(SysHolidayService holidayService) {
        return new PriceCalculator(holidayService);
    }
}
