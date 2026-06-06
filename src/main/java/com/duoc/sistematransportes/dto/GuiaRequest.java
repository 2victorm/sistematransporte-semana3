package com.duoc.sistematransportes.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuiaRequest {

    @NotBlank(message = "El transportista es obligatorio")
    private String transportista;

    @NotBlank(message = "El destinatario es obligatorio")
    private String destinatario;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    private String direccionEntrega;
}