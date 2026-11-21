package pe.utp.policlinico.dao.impl;

import java.util.List;
import java.util.Optional;
import pe.utp.policlinico.dao.PersonalDao;
import pe.utp.policlinico.model.Personal;
import pe.utp.policlinico.repository.PersonalRepository;
import org.springframework.stereotype.Repository;

/** Implementación del DAO de Personal; delega en el repositorio Spring Data JPA. */
@Repository
public class PersonalDaoImpl implements PersonalDao {

    private final PersonalRepository repository;

    public PersonalDaoImpl(PersonalRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Personal> findByDocumento(String documento) {
        return repository.findByDocumento(documento);
    }

    @Override
    public boolean existsByDocumento(String documento) {
        return repository.existsByDocumento(documento);
    }

    @Override
    public List<Personal> listarProfesionales() {
        return repository.listarProfesionales();
    }

    @Override
    public List<Personal> listarProfesionalesPorEspecialidad(Integer idEspecialidad) {
        return repository.listarProfesionalesPorEspecialidad(idEspecialidad);
    }

    @Override
    public Optional<Personal> buscarConEspecialidad(Integer id) {
        return repository.buscarConEspecialidad(id);
    }

    @Override
    public String rolSecundario(Integer id) {
        return repository.rolSecundario(id);
    }

    @Override
    public void insertarCajero(Integer id) {
        repository.insertarCajero(id);
    }

    @Override
    public void insertarCoordinador(Integer id) {
        repository.insertarCoordinador(id);
    }

    @Override
    public Personal getReferenceById(Integer id) {
        return repository.getReferenceById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Personal save(Personal personal) {
        return repository.save(personal);
    }
}
