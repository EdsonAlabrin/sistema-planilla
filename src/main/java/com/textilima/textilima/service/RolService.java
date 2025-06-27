package com.textilima.textilima.service;

import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.Rol;

public interface RolService {
    /**
     * Obtiene una lista de todos los roles.
     * @return Una lista de objetos Rol.
     */
    List<Rol> getAllRoles();

    /**
     * Obtiene un rol por su ID.
     * @param id El ID del rol.
     * @return Un Optional que contiene el Rol si se encuentra, o vacío si no existe.
     */
    Optional<Rol> getRolById(Integer idRol);

    /**
     * Guarda un nuevo rol o actualiza uno existente.
     * @param rol El objeto Rol a guardar/actualizar.
     * @return El Rol guardado/actualizado.
     */
    Rol saveRol(Rol rol);

    /**
     * Elimina un rol por su ID.
     * @param id El ID del rol a eliminar.
     */
    void deleteRol(Integer idRol);

    /**
     * Busca un rol por su nombre.
     * @param nombreRol El nombre del rol.
     * @return Un Optional que contiene el Rol si se encuentra, o vacío si no existe.
     */
    Optional<Rol> getRolByNombreRol(String nombreRol);
}
