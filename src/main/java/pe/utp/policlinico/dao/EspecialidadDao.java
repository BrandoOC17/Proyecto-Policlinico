package pe.utp.policlinico.dao;

import java.util.List;
import java.util.Optional;
import pe.utp.policlinico.model.Especialidad;

/** DAO de Especialidad: contrato de acceso a datos que usa la capa de servicio. */
public interface EspecialidadDao {

    // Lista de especialidades ordenada alfabéticamente.
    List<Especialidad> findAllByOrderByNombreAsc();

    // Evita nombres repetidos al crear una especialidad.
    boolean existsByNombreIgnoreCase(String nombre);

    // Evita nombres repetidos al editar (ignora la propia especialidad).
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);

    // Busca una especialidad por su id.
    Optional<Especialidad> findById(Integer id);

    // Inserta o actualiza una especialidad.
    Especialidad save(Especialidad especialidad);

    // Elimina una especialidad por su id.
    void deleteById(Integer id);
}
