package com.textilima.textilima.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.textilima.textilima.model.Rol;
import com.textilima.textilima.model.Usuario;
import com.textilima.textilima.repository.RolRepository;
import com.textilima.textilima.repository.UsuarioRepository;
import com.textilima.textilima.service.UsuarioService;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {
     @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository; // Para buscar y asignar roles

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyectar el codificador de contraseñas

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> getUsuarioById(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Override
    @Transactional
    public Usuario saveUsuario(Usuario usuario) {
        // Al guardar un usuario, si la contraseña no está encriptada, la encriptamos.
        // Esto es importante si se actualiza un usuario sin cambiar la contraseña,
        // o si se crea un usuario directamente sin pasar por registerNewUser.
        if (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$")) { // Verifica si ya está encriptada (BCrypt)
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void deleteUsuario(Integer idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public List<Usuario> findByRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Override
    @Transactional
    public Usuario registerNewUser(Usuario usuario) {
        // Asegurarse de que la contraseña esté encriptada antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        // Asegurarse de que el usuario esté habilitado por defecto
        usuario.setEnabled(true);
        // La fecha de creación se establece automáticamente por @PrePersist
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public Usuario updatePassword(Integer idUsuario, String newPassword) {
        return usuarioRepository.findById(idUsuario).map(usuario -> {
            usuario.setPassword(passwordEncoder.encode(newPassword));
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
    }

    @Override
    @Transactional
    public Usuario changeUserEnabledStatus(Integer idUsuario, boolean enabled) {
        return usuarioRepository.findById(idUsuario).map(usuario -> {
            usuario.setEnabled(enabled);
            return usuarioRepository.save(usuario);
        }).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));
    }
}
