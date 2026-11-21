package pe.utp.policlinico.dao;

import java.util.Optional;
import pe.utp.policlinico.model.Paciente;

/** DAO de Paciente: contrato de acceso a datos que usa la capa de servicio. */
public interface PacienteDao {

    // Busca una cuenta de paciente por su documento (inicio de sesión).
    Optional<Paciente> findByDocumento(String documento);

    // Indica si ya existe un paciente con ese documento (RF01).
    boolean existsByDocumento(String documento);

    // Busca un paciente por su id.
    Optional<Paciente> findById(Integer id);

    // Inserta o actualiza un paciente.
    Paciente save(Paciente paciente);
}
