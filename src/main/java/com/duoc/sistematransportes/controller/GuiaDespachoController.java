package com.duoc.sistematransportes.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.duoc.sistematransportes.dto.GuiaRequest;
import com.duoc.sistematransportes.dto.GuiaResponse;
import com.duoc.sistematransportes.service.GuiaDespachoService;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/api/guias")
@Validated
public class GuiaDespachoController {

    private final GuiaDespachoService service;

    public GuiaDespachoController(
            GuiaDespachoService service) {

        this.service = service;
    }

    @PostMapping
    public GuiaResponse crearGuia(
            @Valid @RequestBody GuiaRequest request) {

        return service.crearGuia(request);
    }

    @GetMapping
    public List<GuiaResponse> listar() {

        return service.listarGuias();
    }

    @GetMapping("/{id}")
    public GuiaResponse obtenerPorId(
            @PathVariable Long id) {

        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public GuiaResponse actualizar(
            @PathVariable Long id,
            @Valid @RequestBody GuiaRequest request) {

        return service.actualizarGuia(id, request);
    }

    @DeleteMapping("/{id}")
    public void eliminar(
            @PathVariable Long id) {

        service.eliminarGuia(id);
    }

    @GetMapping("/transportista/{nombre}")
    public List<GuiaResponse> buscarTransportista(
            @PathVariable String nombre) {

        return service.buscarPorTransportista(nombre);
    }

    @GetMapping("/fecha/{fecha}")
    public List<GuiaResponse> buscarFecha(
            @PathVariable LocalDate fecha) {

        return service.buscarPorFecha(fecha);
    }

    @GetMapping("/buscar")
    public List<GuiaResponse> buscarTransportistaYFecha(
            @RequestParam String transportista,
            @RequestParam LocalDate fecha) {

        return service.buscarPorTransportistaYFecha(
                transportista,
                fecha);
    }

    @PostMapping("/{id}/subir")
    public String subirAS3(
            @PathVariable Long id) {

        return service.subirAS3(id);
    }

    @PutMapping("/{id}/archivo-s3")
    public String actualizarArchivoEnS3(
            @PathVariable Long id) {

        return service.actualizarArchivoEnS3(id);
    }

    @GetMapping("/{id}/descargar")
    public ResponseEntity<byte[]> descargarArchivo(
            @PathVariable Long id) {

        byte[] archivo = service.descargarDesdeS3(id);

        GuiaResponse guia = service.buscarPorId(id);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" +
                                guia.getNumeroGuia() +
                                ".txt\"")
                .body(archivo);
    }

}