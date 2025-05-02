package com.service.powercrm.service.integracao;

import com.service.powercrm.domain.Brand;
import com.service.powercrm.domain.Model;
import com.service.powercrm.domain.SyncMetadata;
import com.service.powercrm.dto.BrandDTO;
import com.service.powercrm.dto.ModelDTO;
import com.service.powercrm.repository.BrandRepository;
import com.service.powercrm.repository.ModelRepository;
import com.service.powercrm.repository.SyncMetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FipeSyncService {

    private static final Duration CACHE_EXPIRATION = Duration.ofDays(7);

    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final SyncMetadataRepository syncRepo;
    private final FipeClient fipeClient;

    @Transactional
    public void syncBrandsAndModels() {
        var meta = syncRepo.findByEntity("brands_models")
                .orElse(SyncMetadata.builder()
                        .entity("brands_models")
                        .lastSync(Instant.EPOCH)
                        .status("PENDING")
                        .message(null)
                        .build());

        if (Duration.between(meta.getLastSync(), Instant.now()).compareTo(CACHE_EXPIRATION) < 0) {
            log.info("Ignorando sincronização: executada recentemente em {}", meta.getLastSync());
            return;
        }

        log.info("Iniciando sincronização de marcas e modelos da FIPE...");

        try {
            BrandDTO[] brands = fipeClient.listBrands();

            if (brands.length == 0) {
                log.warn("Nenhuma marca retornada pela API FIPE.");
                return;
            }

            Set<Long> existingBrandCodes = new HashSet<>(brandRepository.findAllCode());
            List<Brand> newBrands = Arrays.stream(brands)
                    .filter(dto -> !existingBrandCodes.contains(dto.getCode()))
                    .map(dto -> Brand.builder()
                            .code(dto.getCode())
                            .name(dto.getName())
                            .build())
                            .toList();

            if (!newBrands.isEmpty()) {
                brandRepository.saveAll(newBrands);
                log.info("Salvou {} novas marcas.", newBrands.size());
            }

            Set<Long> existingModelIds = new HashSet<>(modelRepository.findAllIds());
            List<Model> allNewModels = new ArrayList<>();

            for (Brand brand : newBrands) {
                ModelDTO[] models = fipeClient.listModels(brand.getCode());

                for (ModelDTO modelDTO : models) {
                    if (!existingModelIds.contains(modelDTO.getCode())) {
                        allNewModels.add(Model.builder()
                                .code(modelDTO.getCode())
                                .name(modelDTO.getName())
                                .brand(brand)
                                .build());
                        existingModelIds.add(modelDTO.getCode());
                    }
                }
            }

            if (!allNewModels.isEmpty()) {
                modelRepository.saveAll(allNewModels);
                log.info("Salvou {} novos modelos.", allNewModels.size());
            }

            meta.setLastSync(Instant.now());
            meta.setStatus("SUCCESS");
            meta.setMessage("Sincronização concluída com sucesso.");
            syncRepo.save(meta);

        } catch (Exception e) {
            log.error("Erro ao sincronizar marcas e modelos da API FIPE.", e);

            meta.setStatus("FAILURE");
            meta.setMessage(e.getMessage());
            syncRepo.save(meta);

            throw e;
        }
    }
}
