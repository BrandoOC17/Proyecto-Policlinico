package pe.utp.policlinico.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import pe.utp.policlinico.model.CitaMedica;

/** DAO de CitaMedica: contrato de acceso a datos que usa la capa de servicio. */
public interface CitaMedicaDao {

    // Busca una cita por su id con todos sus datos cargados.
    Optional<CitaMedica> findById(Integer id);

    // Historial de citas de un paciente.
    List<CitaMedica> listarPorPaciente(Integer idPaciente);

    // Citas reservadas del turno de un profesional (CU09).
    List<CitaMedica> listarReservadasPorProfesional(Integer idProfesional, LocalDate hoy);

    // Todas las citas para el panel del coordinador (CU08).
    List<CitaMedica> listarTodas();

    // Citas que la caja puede cobrar: no canceladas y sin pago registrado (CU11).
    List<CitaMedica> listarPendientesDePago();

    // Cantidad de citas por estado: [estado, cantidad].
    List<Object[]> contarPorEstado();

    // Indica si un horario ya tiene citas históricas (no puede eliminarse).
    boolean existsByHorario_Id(Integer idHorario);

    // Inserta o actualiza una cita.
    CitaMedica save(CitaMedica cita);
}
