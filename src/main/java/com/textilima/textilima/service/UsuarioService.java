package com.textilima.textilima.service;

import java.util.List;
import java.util.Optional;

import com.textilima.textilima.model.Rol;
import com.textilima.textilima.model.Usuario;

public interface UsuarioService {
    List<Usuario> getAllUsuarios();
    Optional<Usuario> getUsuarioById(Integer idUsuario);
    Usuario saveUsuario(Usuario usuario);
    void deleteUsuario(Integer idUsuario);
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(Rol rol);

    // Método para guardar un nuevo usuario, encriptando la contraseña
    Usuario registerNewUser(Usuario usuario);

    // Método para actualizar la contraseña de un usuario
    Usuario updatePassword(Integer idUsuario, String newPassword);

    // Método para cambiar el estado de habilitación de un usuario
    Usuario changeUserEnabledStatus(Integer idUsuario, boolean enabled);
}
