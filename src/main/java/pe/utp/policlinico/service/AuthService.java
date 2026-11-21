package pe.utp.policlinico.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.policlinico.config.Roles;
import pe.utp.policlinico.dao.PacienteDao;
import pe.utp.policlinico.dao.PersonalDao;
import pe.utp.policlinico.dto.UsuarioSesion;
import pe.utp.policlinico.exception.NegocioException;
import pe.utp.policlinico.model.Paciente;
import pe.utp.policlinico.model.Personal;

@Service
public class AuthService {

    private final PacienteDao pacienteDao;
    private final PersonalDao personalDao;
    private final PasswordEncoder passwordEncoder;

    public AuthService(PacienteDao pacienteDao, PersonalDao personalDao,
                       PasswordEncoder passwordEncoder) {
        this.pacienteDao = pacienteDao;
        this.personalDao = personalDao;
        this.passwordEncoder = passwordEncoder;
    }

    // CU02 - Iniciar sesión por documento y contraseña. El rol se deduce de las tablas.
    @Transactional(readOnly = true)
    public UsuarioSesion autenticar(String documento, String password) {
        if (documento == null || password == null || documento.isBlank() || password.isBlank()) {
            throw new NegocioException("Ingresa tu documento y tu contraseña.");
        }
        String doc = documento.trim();

        Paciente paciente = pacienteDao.findByDocumento(doc).orElse(null);
        if (paciente != null && passwordEncoder.matches(password, paciente.getContrasena())) {
            return new UsuarioSesion(paciente.getId(), paciente.getNombres(), paciente.getApellidos(),
                    paciente.getDocumento(), Roles.PACIENTE);
        }

        Personal personal = personalDao.findByDocumento(doc).orElse(null);
        if (personal != null && passwordEncoder.matches(password, personal.getContrasena())) {
            String rol = personal.getEspecialidad() != null
                    ? Roles.PROFESIONAL
                    : personalDao.rolSecundario(personal.getId());
            if (rol != null) {
                return new UsuarioSesion(personal.getId(), personal.getNombres(), personal.getApellidos(),
                        personal.getDocumento(), rol);
            }
        }
        throw new NegocioException("Documento o contraseña incorrectos.");
    }
}
