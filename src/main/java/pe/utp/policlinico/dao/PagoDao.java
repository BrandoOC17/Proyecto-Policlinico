package pe.utp.policlinico.dao;

import java.math.BigDecimal;
import java.util.Optional;
import pe.utp.policlinico.dto.ComprobanteDto;
import pe.utp.policlinico.model.Pago;

/** DAO de Pago: contrato de acceso a datos que usa la capa de servicio. */
public interface PagoDao {

    // Total cobrado (panel del coordinador).
    BigDecimal totalRecaudado();

    // Inserta o actualiza un pago.
    Pago save(Pago pago);

    // Inserta el comprobante del pago (tabla comprobante_pago).
    void insertarComprobante(String datosPaciente, String datosCita, BigDecimal monto, String formaPago, Integer idPago);

    // Comprobante asociado a una cita.
    Optional<ComprobanteDto> buscarComprobantePorCita(Integer idCita);
}
