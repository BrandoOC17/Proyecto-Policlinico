package pe.utp.policlinico.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.policlinico.config.Roles;
import pe.utp.policlinico.dao.CitaMedicaDao;
import pe.utp.policlinico.dao.HorarioDao;
import pe.utp.policlinico.dao.PacienteDao;
import pe.utp.policlinico.dao.PersonalDao;
import pe.utp.policlinico.dto.UsuarioSesion;
import pe.utp.policlinico.exception.NegocioException;
import pe.utp.policlinico.model.CitaMedica;
import pe.utp.policlinico.model.Horario;
import pe.utp.policlinico.model.Paciente;
import pe.utp.policlinico.model.Personal;

@Service
public class CitaService {

    private final CitaMedicaDao citaDao;
    private final HorarioDao horarioDao;
    private final PacienteDao pacienteDao;
    private final PersonalDao personalDao;

    public CitaService(CitaMedicaDao citaDao, HorarioDao horarioDao,
                       PacienteDao pacienteDao, PersonalDao personalDao) {
        this.citaDao = citaDao;
        this.horarioDao = horarioDao;
        this.pacienteDao = pacienteDao;
        this.personalDao = personalDao;
    }

    // CU05 - Reserva una cita. La transacción bloquea el horario para evitar duplicidad.
    @Transactional
    public Integer reservar(Integer idPaciente, Integer idEspecialidad, Integer idProfesional, Integer idHorario) {
        Horario h = horarioDao.bloquear(idHorario)
                .orElseThrow(() -> new NegocioException("El horario seleccionado no existe."));
        if (!"DISPONIBLE".equals(h.getEstado())) {
            throw new NegocioException("Ese horario acaba de ser reservado por otro paciente. Elige otro.");
        }
        if (!h.getProfesional().getId().equals(idProfesional)) {
            throw new NegocioException("El horario no corresponde al profesional elegido.");
        }
        if (LocalDateTime.of(h.getFecha(), h.getHora()).isBefore(LocalDateTime.now())) {
            throw new NegocioException("No se puede reservar un horario pasado.");
        }

        Personal profesional = personalDao.buscarConEspecialidad(idProfesional)
                .orElseThrow(() -> new NegocioException("El profesional no existe."));
        if (profesional.getEspecialidad() == null
                || !profesional.getEspecialidad().getId().equals(idEspecialidad)) {
            throw new NegocioException("El profesional no pertenece a la especialidad elegida.");
        }
        Paciente paciente = pacienteDao.findById(idPaciente)
                .orElseThrow(() -> new NegocioException("El paciente no existe."));

        CitaMedica cita = new CitaMedica();
        cita.setEstado("RESERVADA");
        cita.setPaciente(paciente);
        cita.setProfesional(profesional);
        cita.setEspecialidad(profesional.getEspecialidad());
        cita.setHorario(h);
        citaDao.save(cita);

        h.setEstado("RESERVADO");
        horarioDao.save(h);
        return cita.getId();
    }

    // CU06 - Cancela una cita (paciente o coordinador) y libera el horario.
    @Transactional
    public void cancelar(Integer idCita, UsuarioSesion usuario) {
        CitaMedica c = citaDao.findById(idCita)
                .orElseThrow(() -> new NegocioException("La cita no existe."));
        boolean esPaciente = Roles.PACIENTE.equals(usuario.rol());
        boolean esCoordinador = Roles.COORDINADOR.equals(usuario.rol());
        if (!esPaciente && !esCoordinador) {
            throw new NegocioException("No tienes permiso para cancelar citas.");
        }
        if (esPaciente && !c.getPaciente().getId().equals(usuario.id())) {
            throw new NegocioException("Solo puedes cancelar tus propias citas.");
        }
        if (!"RESERVADA".equals(c.getEstado())) {
            throw new NegocioException("Solo se pueden cancelar citas en estado RESERVADA.");
        }
        if (c.isPagada()) {
            throw new NegocioException("La cita ya tiene un pago registrado y no puede cancelarse.");
        }
        c.setEstado("CANCELADA");
        c.getHorario().setEstado("DISPONIBLE");
        citaDao.save(c);
        horarioDao.save(c.getHorario());
    }

    public List<CitaMedica> citasDelPaciente(Integer idPaciente) {
        return citaDao.listarPorPaciente(idPaciente);
    }

    // CU09 - Citas reservadas del turno de un profesional.
    public List<CitaMedica> citasDelTurno(Integer idProfesional) {
        return citaDao.listarReservadasPorProfesional(idProfesional, LocalDate.now());
    }

    public CitaMedica citaDelProfesional(Integer idCita, Integer idProfesional) {
        CitaMedica c = citaDao.findById(idCita)
                .orElseThrow(() -> new NegocioException("La cita no existe."));
        if (!c.getProfesional().getId().equals(idProfesional)) {
            throw new NegocioException("Esa cita no está asignada a ti.");
        }
        return c;
    }

    // CU10 - Registra la atención y pasa la cita a FINALIZADA.
    @Transactional
    public void finalizar(Integer idCita, Integer idProfesional, String registroAtencion) {
        CitaMedica c = citaDelProfesional(idCita, idProfesional);
        if (!"RESERVADA".equals(c.getEstado())) {
            throw new NegocioException("Solo se puede finalizar una cita reservada.");
        }
        if (registroAtencion == null || registroAtencion.isBlank()) {
            throw new NegocioException("Debes registrar la atención realizada para finalizar la cita.");
        }
        c.setRegistroAtencion(registroAtencion.trim());
        c.setEstado("FINALIZADA");
        citaDao.save(c);
    }

    // CU08 - Todas las citas y el conteo por estado para el panel del coordinador.
    public List<CitaMedica> todas() {
        return citaDao.listarTodas();
    }

    public Map<String, Integer> contarPorEstado() {
        Map<String, Integer> r = new LinkedHashMap<>();
        r.put("RESERVADA", 0);
        r.put("FINALIZADA", 0);
        r.put("CANCELADA", 0);
        for (Object[] fila : citaDao.contarPorEstado()) {
            r.put((String) fila[0], ((Number) fila[1]).intValue());
        }
        return r;
    }
}
