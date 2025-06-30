package com.textilima.textilima.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    /**
     * Busca un rol por su nombre.
     * @param nombreRol El nombre del rol a buscar.
     * @return El rol encontrado o null si no existe.
     */
    Rol findByNombreRol(String nombreRol);
}
