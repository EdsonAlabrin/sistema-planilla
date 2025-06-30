package com.textilima.textilima.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.textilima.textilima.model.Rol;
import com.textilima.textilima.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
     /**
     * Busca un usuario por su nombre de usuario.
     * @param username El nombre de usuario a buscar.
     * @return Un Optional que contiene el Usuario encontrado, o vacío si no existe.
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Busca un usuario por el ID de empleado asociado.
     * Esto es útil para verificar si un empleado ya tiene una cuenta de usuario.
     * @param empleadoId El ID del empleado asociado.
     * @return Un Optional que contiene el Usuario encontrado, o vacío si no existe.
     */
    Optional<Usuario> findByEmpleadoIdEmpleado(Integer idEmpleado);

    /**
     * Busca todos los usuarios que tienen un rol específico.
     * @param rol El objeto Rol del cual buscar usuarios.
     * @return Una lista de usuarios que tienen el rol especificado.
     */
    List<Usuario> findByRol(Rol rol);
}
