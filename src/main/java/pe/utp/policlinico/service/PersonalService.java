package pe.utp.policlinico.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.policlinico.config.Roles;
import pe.utp.policlinico.dao.EspecialidadDao;
import pe.utp.policlinico.dao.PersonalDao;
import pe.utp.policlinico.dto.PersonalForm;
import pe.utp.policlinico.exception.NegocioException;
import pe.utp.policlinico.model.Especialidad;
import pe.utp.policlinico.model.Personal;

@Service
public class PersonalService {

    private final PersonalDao personalDao;
    private final EspecialidadDao especialidadDao;
    private final PasswordEncoder passwordEncoder;

    public PersonalService(PersonalDao personalDao, EspecialidadDao especialidadDao,
                           PasswordEncoder passwordEncoder) {
        this.personalDao = personalDao;
        this.especialidadDao = especialidadDao;
        this.passwordEncoder = passwordEncoder;
    }

    // CU03 - Registra un profesional de salud o un cajero.
    @Transactional
    public Integer registrar(PersonalForm f) {
        String documento = f.getDocumento().trim();
        if (personalDao.existsByDocumento(documento)) {
            throw new NegocioException("Ya existe personal registrado con ese documento.");
        }

        Personal p = nuevoPersonal(f.getNombres(), f.getApellidos(), documento, f.getCorreo(), f.getPassword());

        if (Roles.PROFESIONAL.equals(f.getRol())) {
            if (f.getIdEspecialidad() == null) {
                throw new NegocioException("Selecciona una especialidad para el profesional.");
            }
            Especialidad esp = especialidadDao.findById(f.getIdEspecialidad())
                    .orElseThrow(() -> new NegocioException("La especialidad no existe."));
            p.setEspecialidad(esp); // se guarda en profesional_salud
            return personalDao.save(p).getId();
        }

        Integer id = personalDao.save(p).getId();
        personalDao.insertarCajero(id);
        return id;
    }

    // Usado por el DataSeeder para crear la cuenta inicial del coordinador.
    @Transactional
    public Integer registrarCoordinador(String nombres, String apellidos, String documento, String correo,
                                        String password) {
        Integer id = personalDao.save(nuevoPersonal(nombres, apellidos, documento, correo, password)).getId();
        personalDao.insertarCoordinador(id);
        return id;
    }

    public List<Personal> listarProfesionales() {
        return personalDao.listarProfesionales();
    }

    public List<Personal> profesionalesDe(Integer idEspecialidad) {
        return personalDao.listarProfesionalesPorEspecialidad(idEspecialidad);
    }

    public Personal buscarProfesional(Integer id) {
        Personal p = personalDao.buscarConEspecialidad(id)
                .orElseThrow(() -> new NegocioException("El profesional no existe."));
        if (p.getEspecialidad() == null) {
            throw new NegocioException("El personal seleccionado no es profesional de salud.");
        }
        return p;
    }

    // CU07 - Cambia la especialidad de un profesional.
    @Transactional
    public void cambiarEspecialidad(Integer idProfesional, Integer idEspecialidad) {
        Personal p = buscarProfesional(idProfesional);
        Especialidad esp = especialidadDao.findById(idEspecialidad)
                .orElseThrow(() -> new NegocioException("La especialidad no existe."));
        p.setEspecialidad(esp);
        personalDao.save(p);
    }

    private Personal nuevoPersonal(String nombres, String apellidos, String documento, String correo,
                                   String password) {
        Personal p = new Personal();
        p.setNombres(nombres.trim());
        p.setApellidos(apellidos.trim());
        p.setDocumento(documento.trim());
        p.setCorreo(correo.trim());
        p.setContrasena(passwordEncoder.encode(password));
        return p;
    }
}
