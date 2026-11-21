package pe.utp.policlinico.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/** Tabla pago. El comprobante (tabla comprobante_pago) se maneja en PagoRepository. */
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPago")
    private Integer id;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    /** EFECTIVO o TARJETA */
    @Column(name = "formaPago", nullable = false, length = 30)
    private String formaPago;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCita", nullable = false, unique = true)
    private CitaMedica cita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCajero", nullable = false)
    private Personal cajero;

    public Pago() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public CitaMedica getCita() {
        return cita;
    }

    public void setCita(CitaMedica cita) {
        this.cita = cita;
    }

    public Personal getCajero() {
        return cajero;
    }

    public void setCajero(Personal cajero) {
        this.cajero = cajero;
    }
}
