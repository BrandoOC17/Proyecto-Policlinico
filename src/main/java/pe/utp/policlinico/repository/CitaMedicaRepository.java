package pe.utp.policlinico.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.utp.policlinico.model.CitaMedica;

public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Integer> {

    // Carga de una vez los datos que muestran las vistas (evita consultas repetidas).
    @Override
    @EntityGraph(attributePaths = {"paciente", "profesional", "especialidad", "horario", "pago"})
    Optional<CitaMedica> findById(Integer id);

    // Historial de citas de un paciente.
    @EntityGraph(attributePaths = {"paciente", "profesional", "especialidad", "horario", "pago"})
    @Query("select c from CitaMedica c where c.paciente.id = :idPaciente "
            + "order by c.horario.fecha desc, c.horario.hora desc")
    List<CitaMedica> listarPorPaciente(@Param("idPaciente") Integer idPaciente);

    // Citas reservadas del turno de un profesional (CU09).
    @EntityGraph(attributePaths = {"paciente", "profesional", "especialidad", "horario", "pago"})
    @Query("select c from CitaMedica c where c.profesional.id = :idProfesional and c.estado = 'RESERVADA' "
            + "and c.horario.fecha >= :hoy order by c.horario.fecha, c.horario.hora")
    List<CitaMedica> listarReservadasPorProfesional(@Param("idProfesional") Integer idProfesional,
                                                    @Param("hoy") LocalDate hoy);

    // Todas las citas para el panel del coordinador (CU08).
    @EntityGraph(attributePaths = {"paciente", "profesional", "especialidad", "horario", "pago"})
    @Query("select c from CitaMedica c order by c.horario.fecha desc, c.horario.hora desc")
    List<CitaMedica> listarTodas();

    // Citas que la caja puede cobrar: no canceladas y sin pago registrado (CU11).
    @EntityGraph(attributePaths = {"paciente", "profesional", "especialidad", "horario", "pago"})
    @Query("select c from CitaMedica c where c.estado <> 'CANCELADA' "
            + "and not exists (select 1 from Pago p where p.cita = c) "
            + "order by c.horario.fecha, c.horario.hora")
    List<CitaMedica> listarPendientesDePago();

    // Cantidad de citas por estado: [estado, cantidad].
    @Query("select c.estado, count(c) from CitaMedica c group by c.estado")
    List<Object[]> contarPorEstado();

    // Un horario con citas históricas no puede eliminarse.
    boolean existsByHorario_Id(Integer idHorario);
}
