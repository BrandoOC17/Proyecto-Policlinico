package pe.utp.policlinico.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import pe.utp.policlinico.dao.EspecialidadDao;
import pe.utp.policlinico.exception.NegocioException;
import pe.utp.policlinico.model.Especialidad;

@Service
public class EspecialidadService {

    private final EspecialidadDao especialidadDao;

    public EspecialidadService(EspecialidadDao especialidadDao) {
        this.especialidadDao = especialidadDao;
    }

    public List<Especialidad> listar() {
        return especialidadDao.findAllByOrderByNombreAsc();
    }

    // CU07 - Crea (id vacío) o actualiza una especialidad.
    public void guardar(Integer id, String nombre, String tarifaTexto) {
        if (nombre == null || nombre.isBlank()) {
            throw new NegocioException("El nombre de la especialidad es obligatorio.");
        }
        BigDecimal tarifa;
        try {
            tarifa = new BigDecimal(tarifaTexto.trim());
        } catch (RuntimeException e) {
            throw new NegocioException("La tarifa no es un número válido.");
        }
        if (tarifa.signum() < 0) {
            throw new NegocioException("La tarifa no puede ser negativa.");
        }

        String nombreLimpio = nombre.trim();
        Especialidad e;
        if (id == null) {
            if (especialidadDao.existsByNombreIgnoreCase(nombreLimpio)) {
                throw new NegocioException("Ya existe una especialidad con ese nombre.");
            }
            e = new Especialidad();
        } else {
            if (especialidadDao.existsByNombreIgnoreCaseAndIdNot(nombreLimpio, id)) {
                throw new NegocioException("Ya existe una especialidad con ese nombre.");
            }
            e = especialidadDao.findById(id)
                    .orElseThrow(() -> new NegocioException("La especialidad no existe."));
        }
        e.setNombre(nombreLimpio);
        e.setTarifa(tarifa);
        especialidadDao.save(e);
    }

    // Elimina una especialidad. Si algún profesional o cita la usa, la BD lo impide y se avisa.
    public void eliminar(Integer id) {
        try {
            especialidadDao.deleteById(id);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new NegocioException("La especialidad no existe.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new NegocioException(
                    "No se puede eliminar: hay profesionales o citas que usan esta especialidad.");
        }
    }
}
