package com.fluxfinanciers.dto.request;

import com.fluxfinanciers.enums.TypeCharge;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChargeRequest {

    @NotBlank(message = "Le nom de la charge est obligatoire")
    @Size(max = 200, message = "Le nom ne peut pas dépasser 200 caractères")
    private String nomCharge;

    @NotNull(message = "Le type de charge est obligatoire")
    private TypeCharge typeCharge;

    @NotNull(message = "Le créateur est obligatoire")
    private Long createdById;
}
