package pe.utp.policlinico.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import pe.utp.policlinico.model.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Integer> {

    // Solo horarios libres y futuros de un profesional (CU04).
    @Query("select h from Horario h where h.profesional.id = :idProfesional and h.estado = 'DISPONIBLE' "
            + "and (h.fecha > :hoy or (h.fecha = :hoy and h.hora > :ahora)) order by h.fecha, h.hora")
    List<Horario> listarDisponibles(@Param("idProfesional") Integer idProfesional,
                                    @Param("hoy") LocalDate hoy, @Param("ahora") LocalTime ahora);

    // Horarios programados desde una fecha, con el profesional ya cargado.
    @EntityGraph(attributePaths = {"profesional"})
    List<Horario> findByFechaGreaterThanEqualOrderByFechaAscHoraAsc(LocalDate desde);

    // Bloquea la fila (SELECT ... FOR UPDATE) para que dos pacientes no reserven el mismo horario (CU05).
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select h from Horario h where h.id = :id")
    Optional<Horario> bloquear(@Param("id") Integer id);

    // Evita crear dos veces el mismo bloque (UK idProfesional + fecha + hora).
    boolean existsByProfesional_IdAndFechaAndHora(Integer idProfesional, LocalDate fecha, LocalTime hora);
}
