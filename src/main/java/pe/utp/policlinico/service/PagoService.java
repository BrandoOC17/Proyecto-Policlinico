package pe.utp.policlinico.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.utp.policlinico.dao.CitaMedicaDao;
import pe.utp.policlinico.dao.PagoDao;
import pe.utp.policlinico.dao.PersonalDao;
import pe.utp.policlinico.dto.ComprobanteDto;
import pe.utp.policlinico.exception.NegocioException;
import pe.utp.policlinico.model.CitaMedica;
import pe.utp.policlinico.model.Pago;

@Service
public class PagoService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final PagoDao pagoDao;
    private final CitaMedicaDao citaDao;
    private final PersonalDao personalDao;

    public PagoService(PagoDao pagoDao, CitaMedicaDao citaDao,
                       PersonalDao personalDao) {
        this.pagoDao = pagoDao;
        this.citaDao = citaDao;
        this.personalDao = personalDao;
    }

    public List<CitaMedica> citasPendientesDePago() {
        return citaDao.listarPendientesDePago();
    }

    public BigDecimal totalRecaudado() {
        return pagoDao.totalRecaudado();
    }

    // CU11 - Verifica los datos de la cita antes de cobrar.
    public CitaMedica verificarCita(Integer idCita) {
        CitaMedica c = citaDao.findById(idCita)
                .orElseThrow(() -> new NegocioException("La cita no existe."));
        if ("CANCELADA".equals(c.getEstado())) {
            throw new NegocioException("La cita está cancelada: no se puede cobrar.");
        }
        if (c.isPagada()) {
            throw new NegocioException("La cita ya tiene un pago registrado.");
        }
        return c;
    }

    // CU11 + CU12 - Registra el pago (monto = tarifa) y genera el comprobante en una sola transacción.
    @Transactional
    public void registrarPago(Integer idCita, String formaPago, Integer idCajero) {
        if (!"EFECTIVO".equals(formaPago) && !"TARJETA".equals(formaPago)) {
            throw new NegocioException("La forma de pago debe ser EFECTIVO o TARJETA.");
        }
        CitaMedica c = verificarCita(idCita);
        BigDecimal monto = c.getTarifa();

        Pago pago = new Pago();
        pago.setMonto(monto);
        pago.setFormaPago(formaPago);
        pago.setCita(c);
        pago.setCajero(personalDao.getReferenceById(idCajero));
        pagoDao.save(pago);

        String datosPaciente = c.getPacienteNombre() + " - Doc. " + c.getDocumentoPaciente();
        String datosCita = "Cita N° " + c.getId() + " - " + c.getEspecialidadNombre() + " - "
                + c.getProfesionalNombre() + " - " + c.getFecha().format(FECHA) + " " + c.getHora().format(HORA);
        pagoDao.insertarComprobante(datosPaciente, datosCita, monto, formaPago, pago.getId());
    }

    // CU12 - Comprobante a imprimir.
    public ComprobanteDto comprobanteDeCita(Integer idCita) {
        return pagoDao.buscarComprobantePorCita(idCita)
                .orElseThrow(() -> new NegocioException("La cita aún no tiene comprobante."));
    }
}
