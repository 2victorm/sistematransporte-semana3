package com.duoc.sistematransportes.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuiaResponse {

    private Long id;

    private String numeroGuia;

    private String transportista;

    private String destinatario;

    private LocalDate fecha;
}