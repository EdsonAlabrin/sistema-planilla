package com.textilima.textilima.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.textilima.textilima.model.Usuario;
import com.textilima.textilima.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;

@Service // Marca esta clase como un componente de servicio de Spring
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carga los detalles del usuario por su nombre de usuario.
     * Este método es invocado por Spring Security durante el proceso de autenticación.
     *
     * @param username el nombre de usuario del usuario a cargar
     * @return un objeto UserDetails que representa al usuario autenticado
     * @throws UsernameNotFoundException si el usuario no es encontrado
     */
    @Override
    @Transactional(readOnly = true) // La transacción es de solo lectura para mejorar el rendimiento
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario en tu base de datos por el nombre de usuario
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con username: " + username));

        // Crea una colección de GrantedAuthority a partir de los roles del usuario
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        // Para una relación ManyToOne (un usuario tiene un solo rol):
        grantedAuthorities.add(new SimpleGrantedAuthority(usuario.getRol().getNombreRol()));

        // Para una relación ManyToMany (si un usuario pudiera tener múltiples roles, como en mi sugerencia inicial):
        // usuario.getRoles().forEach(rol -> grantedAuthorities.add(new SimpleGrantedAuthority(rol.getName())));

        // Retorna un objeto UserDetails de Spring Security
        // El constructor de User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities)
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.isEnabled(), // Usar el campo 'enabled' de tu entidad Usuario
                true, // accountNonExpired (la cuenta nunca expira)
                true, // credentialsNonExpired (las credenciales nunca expiran)
                true, // accountNonLocked (la cuenta nunca se bloquea)
                grantedAuthorities
        );
    }
}