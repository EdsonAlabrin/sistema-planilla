package com.textilima.textilima.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.textilima.textilima.model.Empleado;
import com.textilima.textilima.model.Rol;
import com.textilima.textilima.model.Usuario;
import com.textilima.textilima.repository.UsuarioRepository;
import com.textilima.textilima.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired // Injects the UsuarioRepository dependency
    private UsuarioRepository usuarioRepository;

    /**
     * Retrieves a list of all users.
     * @return A list of Usuario objects.
     */
    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    /**
     * Retrieves a user by their ID.
     * @param id The ID of the user.
     * @return An Optional containing the Usuario if found, or empty if not found.
     */
    @Override
    public Optional<Usuario> getUsuarioById(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    /**
     * Saves a new user or updates an existing one.
     * @param usuario The Usuario object to save/update.
     * @return The saved/updated Usuario.
     */
    @Override
    public Usuario saveUsuario(Usuario usuario) {
        // Here you could add additional business logic before saving,
        // for example, password hashing or validation for unique username/employee association.
        return usuarioRepository.save(usuario);
    }

    /**
     * Deletes a user by their ID.
     * @param id The ID of the user to delete.
     */
    @Override
    public void deleteUsuario(Integer idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    /**
     * Searches for a user by their username.
     * @param username The username to search for.
     * @return An Optional containing the found Usuario, or empty if not found.
     */
    @Override
    public Optional<Usuario> getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Searches for a user by the associated employee.
     * @param empleado The Empleado object associated with the user.
     * @return An Optional containing the found Usuario, or empty if not found.
     */
    @Override
    public Optional<Usuario> getUsuarioByEmpleado(Empleado empleado) {
        // Assuming your UsuarioRepository has findByEmpleadoIdEmpleado(Integer empleadoId)
        // You would use empleado.getIdEmpleado() here.
        return usuarioRepository.findByEmpleadoIdEmpleado(empleado.getIdEmpleado());
    }

    /**
     * Searches for all users who have a specific role.
     * @param rol The role of the users to search for.
     * @return A list of users who have the specified role.
     */
    @Override
    public List<Usuario> getUsuariosByRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }
}
