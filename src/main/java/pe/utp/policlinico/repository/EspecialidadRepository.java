package pe.utp.policlinico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.utp.policlinico.model.Especialidad;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Integer> {

    // Lista para combos y tablas, ordenada alfabéticamente.
    List<Especialidad> findAllByOrderByNombreAsc();

    // Evita nombres repetidos al crear una especialidad.
    boolean existsByNombreIgnoreCase(String nombre);

    // Evita nombres repetidos al editar (ignora la propia especialidad).
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
}
