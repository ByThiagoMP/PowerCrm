package com.service.powercrm.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSummaryDTO {
    private Long id;
    private String name;
    private String email;
}