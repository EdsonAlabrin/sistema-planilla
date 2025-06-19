package com.textilima.textilima.repository;

import com.textilima.textilima.entities.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PuestoRepository extends JpaRepository<Puesto, Integer> {
    // Encuentra un puesto por su nombre
    Optional<Puesto> findByNombrePuesto(String nombrePuesto);
}
