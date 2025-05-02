package com.service.powercrm.kafka;

import com.service.powercrm.domain.Vehicle;
import com.service.powercrm.dto.FipeResponse;
import com.service.powercrm.dto.VehicleValidationEvent;
import com.service.powercrm.repository.VehicleRepository;
import com.service.powercrm.service.FipeService;
import com.service.powercrm.util.MonetaryConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VehicleKafkaConsumer {

    private final FipeService fipeService; // <- agora usa o service
    private final VehicleRepository vehicleRepository;

    @KafkaListener(topics = "vehicle-created", groupId = "vehicle-consumer-group")
    public void listen(VehicleValidationEvent event) {
        try {
            FipeResponse fipe = fipeService.consultarPrecoFipe(
                    event.getYear(), event.getBrandId(), event.getModelId());

            Vehicle vehicle = vehicleRepository.findById(event.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

            vehicle.setFipePrice(MonetaryConverter.convertFromMonetaryFormat(fipe.getPrice()));
            vehicleRepository.save(vehicle);

            log.info("Veículo atualizado com sucesso: {} - R${}", vehicle.getId(), vehicle.getFipePrice());

        } catch (Exception e) {
            log.error("Erro ao processar veículo: " + event.getVehicleId(), e);
        }
    }
}
