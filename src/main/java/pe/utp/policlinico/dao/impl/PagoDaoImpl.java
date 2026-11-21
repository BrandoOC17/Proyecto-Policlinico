package pe.utp.policlinico.dao.impl;

import java.math.BigDecimal;
import java.util.Optional;
import pe.utp.policlinico.dao.PagoDao;
import pe.utp.policlinico.dto.ComprobanteDto;
import pe.utp.policlinico.model.Pago;
import pe.utp.policlinico.repository.PagoRepository;
import org.springframework.stereotype.Repository;

/** Implementación del DAO de Pago; delega en el repositorio Spring Data JPA. */
@Repository
public class PagoDaoImpl implements PagoDao {

    private final PagoRepository repository;

    public PagoDaoImpl(PagoRepository repository) {
        this.repository = repository;
    }

    @Override
    public BigDecimal totalRecaudado() {
        return repository.totalRecaudado();
    }

    @Override
    public Pago save(Pago pago) {
        return repository.save(pago);
    }

    @Override
    public void insertarComprobante(String datosPaciente, String datosCita, BigDecimal monto, String formaPago, Integer idPago) {
        repository.insertarComprobante(datosPaciente, datosCita, monto, formaPago, idPago);
    }

    @Override
    public Optional<ComprobanteDto> buscarComprobantePorCita(Integer idCita) {
        return repository.buscarComprobantePorCita(idCita);
    }
}
