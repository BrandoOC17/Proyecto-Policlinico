package pe.utp.policlinico.dao.impl;

import java.util.Optional;
import pe.utp.policlinico.dao.PacienteDao;
import pe.utp.policlinico.model.Paciente;
import pe.utp.policlinico.repository.PacienteRepository;
import org.springframework.stereotype.Repository;

/** Implementación del DAO de Paciente; delega en el repositorio Spring Data JPA. */
@Repository
public class PacienteDaoImpl implements PacienteDao {

    private final PacienteRepository repository;

    public PacienteDaoImpl(PacienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Paciente> findByDocumento(String documento) {
        return repository.findByDocumento(documento);
    }

    @Override
    public boolean existsByDocumento(String documento) {
        return repository.existsByDocumento(documento);
    }

    @Override
    public Optional<Paciente> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Paciente save(Paciente paciente) {
        return repository.save(paciente);
    }
}
