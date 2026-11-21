package pe.utp.policlinico.dao;

import java.util.List;
import java.util.Optional;
import pe.utp.policlinico.model.Personal;

/** DAO de Personal: contrato de acceso a datos que usa la capa de servicio. */
public interface PersonalDao {

    // Busca personal por documento.
    Optional<Personal> findByDocumento(String documento);

    // Indica si ya existe personal con ese documento.
    boolean existsByDocumento(String documento);

    // Profesionales de salud con su especialidad cargada.
    List<Personal> listarProfesionales();

    // Profesionales de una especialidad (combo de la reserva).
    List<Personal> listarProfesionalesPorEspecialidad(Integer idEspecialidad);

    // Un profesional con su especialidad (vacío si no es profesional de salud).
    Optional<Personal> buscarConEspecialidad(Integer id);

    // Rol del personal que no es profesional: CAJERO, COORDINADOR o null.
    String rolSecundario(Integer id);

    // Registra al personal como cajero (tabla subtipo).
    void insertarCajero(Integer id);

    // Registra al personal como coordinador (tabla subtipo).
    void insertarCoordinador(Integer id);

    // Referencia perezosa al personal (sin consultar la base de datos).
    Personal getReferenceById(Integer id);

    // Cantidad de personal registrado.
    long count();

    // Inserta o actualiza personal.
    Personal save(Personal personal);
}
