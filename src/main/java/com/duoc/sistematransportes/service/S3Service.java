package com.duoc.sistematransportes.service;

import java.io.File;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.duoc.sistematransportes.entity.GuiaDespacho;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String subirArchivo(GuiaDespacho guia) {

        File archivo = new File(guia.getRutaArchivo());

        String key = guia.getFecha()
                + "/"
                + limpiarNombreCarpeta(guia.getTransportista())
                + "/"
                + archivo.getName();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromFile(
                        Paths.get(guia.getRutaArchivo())));

        return key;
    }

    public ResponseInputStream<GetObjectResponse> obtenerArchivo(String key) {

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        return s3Client.getObject(request);
    }

    public void eliminarArchivo(String key) {

        if (key == null || key.isBlank()) {
            return;
        }

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(request);
    }

    private String limpiarNombreCarpeta(String texto) {
        return texto.trim().replaceAll("\\s+", "_");
    }
}
