package pe.utp.policlinico.dao.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import pe.utp.policlinico.dao.HorarioDao;
import pe.utp.policlinico.model.Horario;
import pe.utp.policlinico.repository.HorarioRepository;
import org.springframework.stereotype.Repository;

/** Implementación del DAO de Horario; delega en el repositorio Spring Data JPA. */
@Repository
public class HorarioDaoImpl implements HorarioDao {

    private final HorarioRepository repository;

    public HorarioDaoImpl(HorarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Horario> listarDisponibles(Integer idProfesional, LocalDate hoy, LocalTime ahora) {
        return repository.listarDisponibles(idProfesional, hoy, ahora);
    }

    @Override
    public List<Horario> findByFechaGreaterThanEqualOrderByFechaAscHoraAsc(LocalDate desde) {
        return repository.findByFechaGreaterThanEqualOrderByFechaAscHoraAsc(desde);
    }

    @Override
    public Optional<Horario> bloquear(Integer id) {
        return repository.bloquear(id);
    }

    @Override
    public boolean existsByProfesional_IdAndFechaAndHora(Integer idProfesional, LocalDate fecha, LocalTime hora) {
        return repository.existsByProfesional_IdAndFechaAndHora(idProfesional, fecha, hora);
    }

    @Override
    public Optional<Horario> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Horario save(Horario horario) {
        return repository.save(horario);
    }

    @Override
    public void delete(Horario horario) {
        repository.delete(horario);
    }
}
