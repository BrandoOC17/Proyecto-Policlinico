package pe.utp.policlinico.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/** Respuesta JSON de /api/horarios. */
public record HorarioDto(Integer id, LocalDate fecha, LocalTime hora) { }
