package com.service.powercrm.config;

import com.service.powercrm.service.integracao.FipeSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class FipeDataLoader implements CommandLineRunner {

    private final FipeSyncService fipeSyncService;

    @Override
    public void run(String... args) {
        fipeSyncService.syncBrandsAndModels();
        System.out.println("FIPE data synchronized at startup.");
    }
}