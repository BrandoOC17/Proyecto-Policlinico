package pe.utp.policlinico.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.policlinico.dao.CitaMedicaDao;
import pe.utp.policlinico.dao.HorarioDao;
import pe.utp.policlinico.exception.NegocioException;
import pe.utp.policlinico.model.Horario;
import pe.utp.policlinico.model.Personal;

@Service
public class HorarioService {

    private final HorarioDao horarioDao;
    private final CitaMedicaDao citaDao;
    private final PersonalService personalService;

    public HorarioService(HorarioDao horarioDao, CitaMedicaDao citaDao,
                          PersonalService personalService) {
        this.horarioDao = horarioDao;
        this.citaDao = citaDao;
        this.personalService = personalService;
    }

    // CU04 - Horarios libres y futuros de un profesional.
    public List<Horario> disponibles(Integer idProfesional) {
        return horarioDao.listarDisponibles(idProfesional, LocalDate.now(), LocalTime.now());
    }

    // CU07 - Horarios programados (desde hoy) para la tabla del coordinador.
    public List<Horario> programados() {
        return horarioDao.findByFechaGreaterThanEqualOrderByFechaAscHoraAsc(LocalDate.now());
    }

    // CU07 - Genera bloques de atención entre dos horas. Devuelve cuántos creó.
    @Transactional
    public int generar(Integer idProfesional, LocalDate fecha, LocalTime inicio, LocalTime fin, int minutos) {
        if (fecha.isBefore(LocalDate.now())) {
            throw new NegocioException("La fecha no puede ser anterior a hoy.");
        }
        if (minutos < 10 || minutos > 240) {
            throw new NegocioException("La duración debe estar entre 10 y 240 minutos.");
        }
        if (!inicio.isBefore(fin)) {
            throw new NegocioException("La hora de inicio debe ser menor que la hora de fin.");
        }
        Personal profesional = personalService.buscarProfesional(idProfesional);

        int creados = 0;
        for (LocalTime h = inicio; h.plusMinutes(minutos).isAfter(h) && !h.plusMinutes(minutos).isAfter(fin);
             h = h.plusMinutes(minutos)) {
            if (!horarioDao.existsByProfesional_IdAndFechaAndHora(idProfesional, fecha, h)) {
                Horario nuevo = new Horario();
                nuevo.setProfesional(profesional);
                nuevo.setFecha(fecha);
                nuevo.setHora(h);
                nuevo.setEstado("DISPONIBLE");
                horarioDao.save(nuevo);
                creados++;
            }
        }
        return creados;
    }

    // CU07 - Solo se puede eliminar un horario libre que nunca tuvo citas.
    @Transactional
    public void eliminar(Integer idHorario) {
        Horario h = horarioDao.findById(idHorario)
                .orElseThrow(() -> new NegocioException("El horario no existe."));
        if (!"DISPONIBLE".equals(h.getEstado())) {
            throw new NegocioException("Solo se pueden eliminar horarios disponibles.");
        }
        if (citaDao.existsByHorario_Id(idHorario)) {
            throw new NegocioException("Ese horario tiene citas en el historial y no puede eliminarse.");
        }
        horarioDao.delete(h);
    }
}
