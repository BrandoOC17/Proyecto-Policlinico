package pe.utp.policlinico.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pe.utp.policlinico.dto.ComprobanteDto;
import pe.utp.policlinico.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    Optional<Pago> findByCita_Id(Integer idCita);

    // Total cobrado (para el panel del coordinador).
    @Query("select coalesce(sum(p.monto), 0) from Pago p")
    BigDecimal totalRecaudado();

    // ---- Tabla comprobante_pago (sin entidad propia) ----

    @Modifying
    @Query(value = "INSERT INTO comprobante_pago (datosPaciente, datosCita, monto, formaPago, idPago) "
            + "VALUES (:datosPaciente, :datosCita, :monto, :formaPago, :idPago)", nativeQuery = true)
    void insertarComprobante(@Param("datosPaciente") String datosPaciente, @Param("datosCita") String datosCita,
                             @Param("monto") BigDecimal monto, @Param("formaPago") String formaPago,
                             @Param("idPago") Integer idPago);

    @Query(value = "SELECT cp.idComprobante AS id, cp.datosPaciente AS datosPaciente, cp.datosCita AS datosCita, "
            + "cp.monto AS monto, cp.formaPago AS formaPago "
            + "FROM comprobante_pago cp JOIN pago pg ON pg.idPago = cp.idPago "
            + "WHERE pg.idCita = :idCita", nativeQuery = true)
    Optional<ComprobanteDto> buscarComprobantePorCita(@Param("idCita") Integer idCita);
}
