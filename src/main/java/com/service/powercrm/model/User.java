package com.service.powercrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "user.name.required")
    @Size(max = 200, message = "user.name.size")
    @Column(nullable = false, length = 200)
    private String name;

    @NotBlank(message = "user.email.required")
    @Email(message = "user.email.invalid")
    @Size(max = 200, message = "user.email.size")
    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Size(max = 11, message = "user.phone.size")
    @Column(length = 11)
    private String phone;

    @NotBlank(message = "user.cpf.required")
    @Size(max = 14, message = "user.cpf.size")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Size(max = 8, message = "user.zipcode.size")
    @Column(length = 8, name = "zip_code")
    private String zipCode;

    @Size(max = 200, message = "user.address.size")
    @Column(length = 200)
    private String address;

    @Size(max = 10, message = "user.addressNumber.size")
    @Column(length = 10, name = "address_number")
    private String addressNumber;

    @Size(max = 200, message = "user.complement.size")
    @Column(length = 200)
    private String complement;

    @NotBlank(message = "user.status.required")
    @Size(max = 20, message = "user.status.size")
    @Column(nullable = false, length = 20)
    private String status = "Ativo";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
