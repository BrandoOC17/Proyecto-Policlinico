package pe.utp.policlinico.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pe.utp.policlinico.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    // Busca una cuenta por su documento para iniciar sesión.
    Optional<Paciente> findByDocumento(String documento);

    // No permite registrar dos cuentas con el mismo documento (RF01).
    boolean existsByDocumento(String documento);
}
