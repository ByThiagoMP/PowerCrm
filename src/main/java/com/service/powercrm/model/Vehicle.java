package com.service.powercrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "vehicle.plate.required")
    @Size(max = 7, message = "vehicle.plate.size")
    @Column(nullable = false, length = 7)
    private String plate;

    @NotNull(message = "user.advertised_price.required") // Alterado para @NotNull
    @DecimalMin(value = "0.00", message = "user.advertised_price.min")
    @Digits(integer = 10, fraction = 2, message = "user.advertised_price.size")
    @Column(nullable = false, unique = true, precision = 10, scale = 2)
    private BigDecimal advertisedPrice;

    @NotNull(message = "vehicle.year.required")
    @Min(value = 1900, message = "vehicle.year.min") // Garantir que o ano seja maior ou igual a 1900
    @Max(value = 9999, message = "vehicle.year.max") // Garantir que o ano seja menor ou igual a 9999
    @Column(nullable = false, name = "year") // Adiciona a coluna no banco de dados
    private Integer year;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
