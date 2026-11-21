package pe.utp.policlinico.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

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

@Entity
@Table(name = "cita_medica")
public class CitaMedica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCita")
    private Integer id;

    /** RESERVADA, FINALIZADA o CANCELADA */
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "RESERVADA";

    @Column(name = "registroAtencion", columnDefinition = "TEXT")
    private String registroAtencion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idPaciente", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idProfesional", nullable = false)
    private Personal profesional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEspecialidad", nullable = false)
    private Especialidad especialidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idHorario", nullable = false)
    private Horario horario;

    @OneToOne(mappedBy = "cita")
    private Pago pago;

    public CitaMedica() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getRegistroAtencion() {
        return registroAtencion;
    }

    public void setRegistroAtencion(String registroAtencion) {
        this.registroAtencion = registroAtencion;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Personal getProfesional() {
        return profesional;
    }

    public void setProfesional(Personal profesional) {
        this.profesional = profesional;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    // ---- Datos de apoyo para las vistas (no son columnas) ----

    public String getPacienteNombre() {
        return paciente == null ? "" : paciente.getNombreCompleto();
    }

    public String getDocumentoPaciente() {
        return paciente == null ? "" : paciente.getDocumento();
    }

    public String getProfesionalNombre() {
        return profesional == null ? "" : profesional.getNombreCompleto();
    }

    public String getEspecialidadNombre() {
        return especialidad == null ? "" : especialidad.getNombre();
    }

    public LocalDate getFecha() {
        return horario == null ? null : horario.getFecha();
    }

    public LocalTime getHora() {
        return horario == null ? null : horario.getHora();
    }

    public BigDecimal getTarifa() {
        return especialidad == null ? BigDecimal.ZERO : especialidad.getTarifa();
    }

    public boolean isPagada() {
        return pago != null;
    }
}
