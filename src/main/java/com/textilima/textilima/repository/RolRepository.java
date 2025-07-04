package com.textilima.textilima.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    /**
     * Busca un rol por su nombre.
     * @param nombreRol El nombre del rol a buscar.
     * @return Un Optional que contiene el Rol encontrado, o vacío si no existe.
     */
    Optional<Rol> findByNombreRol(String nombreRol);
}
