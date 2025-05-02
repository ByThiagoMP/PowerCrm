package com.service.powercrm.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;

import com.service.powercrm.dto.VehicleValidationEvent;

@TestConfiguration
public class KafkaMockConfig {

    @Bean
    @Primary
    public KafkaTemplate<String, VehicleValidationEvent> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }
}