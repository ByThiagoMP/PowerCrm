package com.service.powercrm.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import org.hibernate.validator.constraints.br.CPF;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    
    @NotBlank(message = "{user.name.required}")
    @Size(max = 200)
    private String name;

    @NotBlank(message = "{user.email.required}")
    @Email(message = "{user.email.invalid}")
    private String email;

    @Size(max = 11, message = "{user.phone.invalid}")
    @Pattern(regexp = "\\d{10,11}", message = "{user.phone.invalid}")
    private String phone;

    @NotBlank(message = "{user.cpf.required}")
    @CPF(message = "{user.cpf.invalid}")
    private String cpf;

    @Size(max = 8, message = "{user.zipcode.size}")
    private String zipCode;

    private String address;

    private String addressNumber;

    private String complement;

    private String status;
}