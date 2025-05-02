package com.service.powercrm.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @NotNull(message = "user.advertised_price.required")
    @DecimalMin(value = "0.00", message = "user.advertised_price.min")
    @Digits(integer = 10, fraction = 2, message = "user.advertised_price.size")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal advertisedPrice;

    @NotBlank(message = "vehicle.year.required")
    @Size(min = 6, max = 7, message = "vehicle.year.size")
    @Pattern(regexp = "^[0-9]{4}-(?:[1-9]|1[0-2])$", message = "vehicle.year.format")
    @Column(nullable = false, name = "`year`", length = 7)
    private String year;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    @DecimalMin(value = "0.00", message = "user.advertised_price.min")
    @Digits(integer = 10, fraction = 2, message = "user.advertised_price.size")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal fipePrice;
}
