package com.duoc.sistematransportes.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.duoc.sistematransportes.dto.GuiaRequest;
import com.duoc.sistematransportes.dto.GuiaResponse;
import com.duoc.sistematransportes.entity.GuiaDespacho;
import com.duoc.sistematransportes.repository.GuiaDespachoRepository;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@Service
public class GuiaDespachoServiceImpl implements GuiaDespachoService {

    private final GuiaDespachoRepository repository;
    private final ArchivoService archivoService;
    private final S3Service s3Service;

    public GuiaDespachoServiceImpl(
            GuiaDespachoRepository repository,
            ArchivoService archivoService,
            S3Service s3Service) {

        this.repository = repository;
        this.archivoService = archivoService;
        this.s3Service = s3Service;
    }

    @Override
    public GuiaResponse crearGuia(GuiaRequest request) {

        GuiaDespacho guia = new GuiaDespacho();

        guia.setNumeroGuia("G-" + System.currentTimeMillis());
        guia.setTransportista(request.getTransportista());
        guia.setDestinatario(request.getDestinatario());
        guia.setDireccionEntrega(request.getDireccionEntrega());
        guia.setFecha(LocalDate.now());

        repository.save(guia);

        String rutaArchivo = archivoService.generarArchivoTxt(guia);
        guia.setRutaArchivo(rutaArchivo);

        repository.save(guia);

        return convertirResponse(guia);
    }

    @Override
    public List<GuiaResponse> listarGuias() {

        return repository.findAll()
                .stream()
                .map(this::convertirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GuiaResponse buscarPorId(Long id) {

        GuiaDespacho guia = buscarEntidad(id);
        return convertirResponse(guia);
    }

    @Override
    public GuiaResponse actualizarGuia(Long id, GuiaRequest request) {

        GuiaDespacho guia = buscarEntidad(id);

        String rutaAnterior = guia.getRutaArchivo();

        guia.setTransportista(request.getTransportista());
        guia.setDestinatario(request.getDestinatario());
        guia.setDireccionEntrega(request.getDireccionEntrega());

        repository.save(guia);

        archivoService.eliminarArchivoLocal(rutaAnterior);
        String nuevaRuta = archivoService.generarArchivoTxt(guia);
        guia.setRutaArchivo(nuevaRuta);

        if (guia.getRutaS3() != null) {
            String keyAnterior = guia.getRutaS3();
            String nuevaKey = s3Service.subirArchivo(guia);

            if (!keyAnterior.equals(nuevaKey)) {
                s3Service.eliminarArchivo(keyAnterior);
            }

            guia.setRutaS3(nuevaKey);
        }

        repository.save(guia);

        return convertirResponse(guia);
    }

    @Override
    public void eliminarGuia(Long id) {

        GuiaDespacho guia = buscarEntidad(id);

        if (guia.getRutaS3() != null) {
            s3Service.eliminarArchivo(guia.getRutaS3());
        }

        archivoService.eliminarArchivoLocal(guia.getRutaArchivo());
        repository.delete(guia);
    }

    @Override
    public List<GuiaResponse> buscarPorTransportista(String transportista) {

        return repository.findByTransportista(transportista)
                .stream()
                .map(this::convertirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GuiaResponse> buscarPorFecha(LocalDate fecha) {

        return repository.findByFecha(fecha)
                .stream()
                .map(this::convertirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<GuiaResponse> buscarPorTransportistaYFecha(
            String transportista,
            LocalDate fecha) {

        return repository.findByTransportistaAndFecha(transportista, fecha)
                .stream()
                .map(this::convertirResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String subirAS3(Long id) {

        GuiaDespacho guia = buscarEntidad(id);

        if (guia.getRutaArchivo() == null) {
            String rutaArchivo = archivoService.generarArchivoTxt(guia);
            guia.setRutaArchivo(rutaArchivo);
        }

        String key = s3Service.subirArchivo(guia);
        guia.setRutaS3(key);

        repository.save(guia);

        return key;
    }

    @Override
    public String actualizarArchivoEnS3(Long id) {

        GuiaDespacho guia = buscarEntidad(id);

        if (guia.getRutaArchivo() != null) {
            archivoService.eliminarArchivoLocal(guia.getRutaArchivo());
        }

        String rutaArchivo = archivoService.generarArchivoTxt(guia);
        guia.setRutaArchivo(rutaArchivo);

        String keyAnterior = guia.getRutaS3();
        String nuevaKey = s3Service.subirArchivo(guia);

        if (keyAnterior != null && !keyAnterior.equals(nuevaKey)) {
            s3Service.eliminarArchivo(keyAnterior);
        }

        guia.setRutaS3(nuevaKey);
        repository.save(guia);

        return nuevaKey;
    }

    @Override
    public byte[] descargarDesdeS3(Long id) {

        GuiaDespacho guia = buscarEntidad(id);

        if (guia.getRutaS3() == null) {
            throw new RuntimeException("La guía aún no ha sido subida a S3");
        }

        try (ResponseInputStream<GetObjectResponse> archivo =
                s3Service.obtenerArchivo(guia.getRutaS3())) {

            return archivo.readAllBytes();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al descargar archivo desde S3",
                    e);
        }
    }

    private GuiaDespacho buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guía no encontrada"));
    }

    private GuiaResponse convertirResponse(GuiaDespacho guia) {

        return new GuiaResponse(
                guia.getId(),
                guia.getNumeroGuia(),
                guia.getTransportista(),
                guia.getDestinatario(),
                guia.getFecha());
    }
}
