package com.service.powercrm.domain;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "brands")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long code;

    @Size(max = 200, message = "brand.name.size")
    @Column(nullable = false, length = 200)
    private String name;

    @OneToMany(mappedBy = "brand")
    private List<Model> models;
}
