package pe.utp.policlinico.dao.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import pe.utp.policlinico.dao.CitaMedicaDao;
import pe.utp.policlinico.model.CitaMedica;
import pe.utp.policlinico.repository.CitaMedicaRepository;
import org.springframework.stereotype.Repository;

/** Implementación del DAO de CitaMedica; delega en el repositorio Spring Data JPA. */
@Repository
public class CitaMedicaDaoImpl implements CitaMedicaDao {

    private final CitaMedicaRepository repository;

    public CitaMedicaDaoImpl(CitaMedicaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<CitaMedica> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<CitaMedica> listarPorPaciente(Integer idPaciente) {
        return repository.listarPorPaciente(idPaciente);
    }

    @Override
    public List<CitaMedica> listarReservadasPorProfesional(Integer idProfesional, LocalDate hoy) {
        return repository.listarReservadasPorProfesional(idProfesional, hoy);
    }

    @Override
    public List<CitaMedica> listarTodas() {
        return repository.listarTodas();
    }

    @Override
    public List<CitaMedica> listarPendientesDePago() {
        return repository.listarPendientesDePago();
    }

    @Override
    public List<Object[]> contarPorEstado() {
        return repository.contarPorEstado();
    }

    @Override
    public boolean existsByHorario_Id(Integer idHorario) {
        return repository.existsByHorario_Id(idHorario);
    }

    @Override
    public CitaMedica save(CitaMedica cita) {
        return repository.save(cita);
    }
}
