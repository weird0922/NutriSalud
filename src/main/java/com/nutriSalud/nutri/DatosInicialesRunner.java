package com.nutriSalud.nutri;

import com.nutriSalud.nutri.models.Rol;
import com.nutriSalud.nutri.models.Usuario;
import com.nutriSalud.nutri.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatosInicialesRunner implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${nutrisalud.admin.username:admin}")
    private String adminUsername;

    @Value("${nutrisalud.admin.password:Nutri2026!}")
    private String adminPassword;

    @Value("${nutrisalud.ciudadano.default.username:ciudadano1}")
    private String ciudadanoUsername;

    @Value("${nutrisalud.ciudadano.default.password:Ciudadano2026!}")
    private String ciudadanoPassword;

    public DatosInicialesRunner(UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        crearSiNoExiste(adminUsername, adminPassword, Rol.PERSONAL_SALUD, null, "Administrador NutriSalud");
        crearSiNoExiste(ciudadanoUsername, ciudadanoPassword, Rol.CIUDADANO, "00000000", "Ciudadano Ejemplo");
    }

    private void crearSiNoExiste(String username, String password, Rol rol, String dni, String nombreCompleto) {
        if (usuarioRepository.existsByUsername(username)) {
            return;
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setDniPaciente(dni);
        usuario.setNombresCompletos(nombreCompleto);
        usuarioRepository.save(usuario);
    }
}
