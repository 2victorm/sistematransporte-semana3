package com.duoc.sistematransportes.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.sistematransportes.entity.GuiaDespacho;

public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {

    List<GuiaDespacho> findByTransportista(String transportista);

    List<GuiaDespacho> findByFecha(LocalDate fecha);

    List<GuiaDespacho> findByTransportistaAndFecha(String transportista, LocalDate fecha);
}