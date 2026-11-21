package pe.utp.policlinico.dto;

import java.math.BigDecimal;

/** Proyección de la tabla comprobante_pago (se llena con la consulta nativa de PagoRepository). */
public interface ComprobanteDto {
    Integer getId();
    String getDatosPaciente();
    String getDatosCita();
    BigDecimal getMonto();
    String getFormaPago();
}
