package com.nutriSalud.nutri.security;

import com.nutriSalud.nutri.models.Usuario;
import com.nutriSalud.nutri.repositories.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findActivoPorUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario '" + username + "' no encontrado o inactivo"));

        String rolConPrefijo = "ROLE_" + usuario.getRol().name();
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getActivo(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(rolConPrefijo))
        );
    }
}
