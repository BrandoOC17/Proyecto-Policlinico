package pe.utp.policlinico.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;

/** Formulario para generar bloques de atención (CU07). */
public class HorarioForm {

    @NotNull(message = "Selecciona un profesional.")
    private Integer idProfesional;

    @NotNull(message = "Ingresa una fecha válida.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "Ingresa la hora de inicio.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @NotNull(message = "Ingresa la hora de fin.")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaFin;

    @NotNull(message = "Ingresa la duración de cada cita.")
    private Integer minutos;

    public Integer getIdProfesional() { return idProfesional; }
    public void setIdProfesional(Integer idProfesional) { this.idProfesional = idProfesional; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
    public Integer getMinutos() { return minutos; }
    public void setMinutos(Integer minutos) { this.minutos = minutos; }
}
