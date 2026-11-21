package pe.utp.policlinico.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pe.utp.policlinico.dao.PacienteDao;
import pe.utp.policlinico.dto.RegistroPacienteForm;
import pe.utp.policlinico.exception.NegocioException;
import pe.utp.policlinico.model.Paciente;

@Service
public class PacienteService {

    private final PacienteDao pacienteDao;
    private final PasswordEncoder passwordEncoder;

    public PacienteService(PacienteDao pacienteDao, PasswordEncoder passwordEncoder) {
        this.pacienteDao = pacienteDao;
        this.passwordEncoder = passwordEncoder;
    }

    // CU01 - Registra un paciente (no permite dos cuentas con el mismo documento).
    public Integer registrar(RegistroPacienteForm f) {
        if (!f.getPassword().equals(f.getPassword2())) {
            throw new NegocioException("Las contraseñas no coinciden.");
        }
        String documento = f.getDocumento().trim();
        if (pacienteDao.existsByDocumento(documento)) {
            throw new NegocioException("Ya existe una cuenta con ese documento de identidad.");
        }

        Paciente p = new Paciente();
        p.setNombres(f.getNombres().trim());
        p.setApellidos(f.getApellidos().trim());
        p.setDocumento(documento);
        p.setCorreo(f.getCorreo().trim());
        p.setCelular(f.getCelular().trim());
        p.setContrasena(passwordEncoder.encode(f.getPassword()));
        return pacienteDao.save(p).getId();
    }
}
