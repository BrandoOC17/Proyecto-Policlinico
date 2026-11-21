package pe.utp.policlinico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.utp.policlinico.model.Personal;

public interface PersonalRepository extends JpaRepository<Personal, Integer> {

    Optional<Personal> findByDocumento(String documento);

    boolean existsByDocumento(String documento);

    // Profesionales de salud (personal que tiene especialidad asignada) con su especialidad cargada.
    @Query("select p from Personal p join fetch p.especialidad e order by e.nombre, p.apellidos")
    List<Personal> listarProfesionales();

    // Profesionales de una especialidad (combo de la reserva).
    @Query("select p from Personal p join fetch p.especialidad e where e.id = :idEspecialidad order by p.apellidos")
    List<Personal> listarProfesionalesPorEspecialidad(@Param("idEspecialidad") Integer idEspecialidad);

    // Un profesional con su especialidad (null si el personal no es profesional de salud).
    @Query("select p from Personal p left join fetch p.especialidad where p.id = :id")
    Optional<Personal> buscarConEspecialidad(@Param("id") Integer id);

    // Rol del personal que NO es profesional: CAJERO, COORDINADOR o null.
    @Query(value = "SELECT CASE "
            + "WHEN EXISTS (SELECT 1 FROM cajero WHERE idCajero = :id) THEN 'CAJERO' "
            + "WHEN EXISTS (SELECT 1 FROM coordinador WHERE idCoordinador = :id) THEN 'COORDINADOR' "
            + "END", nativeQuery = true)
    String rolSecundario(@Param("id") Integer id);

    // Tablas subtipo que solo guardan el id del personal.
    @Modifying
    @Query(value = "INSERT INTO cajero (idCajero) VALUES (:id)", nativeQuery = true)
    void insertarCajero(@Param("id") Integer id);

    @Modifying
    @Query(value = "INSERT INTO coordinador (idCoordinador) VALUES (:id)", nativeQuery = true)
    void insertarCoordinador(@Param("id") Integer id);
}
