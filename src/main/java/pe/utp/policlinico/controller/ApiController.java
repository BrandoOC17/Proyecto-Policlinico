package pe.utp.policlinico.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.utp.policlinico.dto.HorarioDto;
import pe.utp.policlinico.dto.ProfesionalDto;
import pe.utp.policlinico.service.HorarioService;
import pe.utp.policlinico.service.PersonalService;

/** Endpoints JSON que consume reservar.js para los combos dependientes (CU04). */
@RestController
@RequestMapping("/api")
public class ApiController {

    private final PersonalService personalService;
    private final HorarioService horarioService;

    public ApiController(PersonalService personalService, HorarioService horarioService) {
        this.personalService = personalService;
        this.horarioService = horarioService;
    }

    @GetMapping("/profesionales")
    public List<ProfesionalDto> profesionales(@RequestParam Integer especialidad) {
        return personalService.profesionalesDe(especialidad).stream()
                .map(p -> new ProfesionalDto(p.getId(), p.getNombreCompleto()))
                .toList();
    }

    @GetMapping("/horarios")
    public List<HorarioDto> horarios(@RequestParam Integer profesional) {
        return horarioService.disponibles(profesional).stream()
                .map(h -> new HorarioDto(h.getId(), h.getFecha(), h.getHora()))
                .toList();
    }
}
