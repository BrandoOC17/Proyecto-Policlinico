package pe.utp.policlinico.dao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import pe.utp.policlinico.model.Horario;

/** DAO de Horario: contrato de acceso a datos que usa la capa de servicio. */
public interface HorarioDao {

    // Horarios libres y futuros de un profesional (CU04).
    List<Horario> listarDisponibles(Integer idProfesional, LocalDate hoy, LocalTime ahora);

    // Horarios programados desde una fecha, con el profesional cargado.
    List<Horario> findByFechaGreaterThanEqualOrderByFechaAscHoraAsc(LocalDate desde);

    // Bloquea la fila (SELECT ... FOR UPDATE) para evitar reservas duplicadas (CU05).
    Optional<Horario> bloquear(Integer id);

    // Evita crear dos veces el mismo bloque (profesional + fecha + hora).
    boolean existsByProfesional_IdAndFechaAndHora(Integer idProfesional, LocalDate fecha, LocalTime hora);

    // Busca un horario por su id.
    Optional<Horario> findById(Integer id);

    // Inserta o actualiza un horario.
    Horario save(Horario horario);

    // Elimina un horario.
    void delete(Horario horario);
}
