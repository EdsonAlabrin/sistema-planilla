package com.textilima.textilima.service;

import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.Rol;
import com.textilima.textilima.model.Usuario;

public interface UsuarioService {
    /**
     * Obtiene una lista de todos los usuarios.
     * @return Una lista de objetos Usuario.
     */
    List<Usuario> getAllUsuarios();

    /**
     * Obtiene un usuario por su ID.
     * @param id El ID del usuario.
     * @return Un Optional que contiene el Usuario si se encuentra, o vacío si no existe.
     */
    Optional<Usuario> getUsuarioById(Integer idUsuario);

    /**
     * Guarda un nuevo usuario o actualiza uno existente.
     * @param usuario El objeto Usuario a guardar/actualizar.
     * @return El Usuario guardado/actualizado.
     */
    Usuario saveUsuario(Usuario usuario);

    /**
     * Elimina un usuario por su ID.
     * @param id El ID del usuario a eliminar.
     */
    void deleteUsuario(Integer idusuario);

    /**
     * Busca un usuario por su nombre de usuario.
     * @param username El nombre de usuario a buscar.
     * @return Un Optional que contiene el Usuario encontrado, o vacío si no existe.
     */
    Optional<Usuario> getUsuarioByUsername(String username);

    /**
     * Busca un usuario por el empleado asociado.
     * @param empleado El objeto Empleado asociado al usuario.
     * @return Un Optional que contiene el Usuario encontrado, o vacío si no existe.
     */
    Optional<Usuario> getUsuarioByEmpleado(Empleado empleado);

    /**
     * Busca todos los usuarios que tienen un rol específico.
     * @param rol El rol de los usuarios a buscar.
     * @return Una lista de usuarios que tienen el rol especificado.
     */
    List<Usuario> getUsuariosByRol(Rol rol);
}
