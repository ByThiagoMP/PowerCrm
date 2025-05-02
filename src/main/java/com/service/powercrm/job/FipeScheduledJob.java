package com.service.powercrm.job;

import com.service.powercrm.service.integracao.FipeSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FipeScheduledJob {

    private final FipeSyncService fipeSyncService;

    @Scheduled(cron = "0 0 2 * * *")
    public void syncDaily() {
        fipeSyncService.syncBrandsAndModels();
        System.out.println("FIPE sync completed by scheduler.");
    }
}
