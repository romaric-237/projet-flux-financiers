package com.fluxfinanciers.dto.request;

import com.fluxfinanciers.enums.CategorieCharge;
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

    @NotBlank(message = "Le libellé de la charge est obligatoire")
    @Size(max = 200)
    private String libelle;

    @NotNull(message = "La catégorie de charge est obligatoire")
    private CategorieCharge categorie;

    @NotNull(message = "Le créateur est obligatoire")
    private Long createdById;
}