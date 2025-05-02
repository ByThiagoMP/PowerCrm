package com.service.powercrm.kafka;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import com.service.powercrm.dto.VehicleValidationEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehicleKafkaProducer {

    private final KafkaTemplate<String, VehicleValidationEvent> kafkaTemplate;

    public void sendValidationEvent(VehicleValidationEvent event) {
        kafkaTemplate.send("vehicle-created", event);
        log.info("Evento enviado para o Kafka: " + event.getVehicleId() + " - " + event.getBrandId() + " - "
                + event.getModelId());
    }
}
