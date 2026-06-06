package com.duoc.sistematransportes.service;

import java.time.LocalDate;
import java.util.List;

import com.duoc.sistematransportes.dto.GuiaRequest;
import com.duoc.sistematransportes.dto.GuiaResponse;

public interface GuiaDespachoService {

    GuiaResponse crearGuia(GuiaRequest request);

    List<GuiaResponse> listarGuias();

    GuiaResponse buscarPorId(Long id);

    GuiaResponse actualizarGuia(Long id, GuiaRequest request);

    void eliminarGuia(Long id);

    List<GuiaResponse> buscarPorTransportista(String transportista);

    List<GuiaResponse> buscarPorFecha(LocalDate fecha);

    List<GuiaResponse> buscarPorTransportistaYFecha(
            String transportista,
            LocalDate fecha);

    String subirAS3(Long id);

    String actualizarArchivoEnS3(Long id);

    byte[] descargarDesdeS3(Long id);
}
