package pe.utp.policlinico.dao.impl;

import java.util.List;
import java.util.Optional;
import pe.utp.policlinico.dao.EspecialidadDao;
import pe.utp.policlinico.model.Especialidad;
import pe.utp.policlinico.repository.EspecialidadRepository;
import org.springframework.stereotype.Repository;

/** Implementación del DAO de Especialidad; delega en el repositorio Spring Data JPA. */
@Repository
public class EspecialidadDaoImpl implements EspecialidadDao {

    private final EspecialidadRepository repository;

    public EspecialidadDaoImpl(EspecialidadRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Especialidad> findAllByOrderByNombreAsc() {
        return repository.findAllByOrderByNombreAsc();
    }

    @Override
    public boolean existsByNombreIgnoreCase(String nombre) {
        return repository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id) {
        return repository.existsByNombreIgnoreCaseAndIdNot(nombre, id);
    }

    @Override
    public Optional<Especialidad> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Especialidad save(Especialidad especialidad) {
        return repository.save(especialidad);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
