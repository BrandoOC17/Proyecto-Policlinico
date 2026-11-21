package pe.utp.policlinico.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.utp.policlinico.dto.UsuarioSesion;
import pe.utp.policlinico.exception.NegocioException;
import pe.utp.policlinico.service.CitaService;
import pe.utp.policlinico.service.EspecialidadService;

@Controller
@RequestMapping("/paciente")
public class PacienteController {

    private final CitaService citaService;
    private final EspecialidadService especialidadService;

    public PacienteController(CitaService citaService, EspecialidadService especialidadService) {
        this.citaService = citaService;
        this.especialidadService = especialidadService;
    }

    @GetMapping("/citas")
    public String misCitas(@SessionAttribute("usuario") UsuarioSesion u, Model model) {
        model.addAttribute("citas", citaService.citasDelPaciente(u.id()));
        return "paciente/citas";
    }

    @GetMapping("/reservar")
    public String reservarForm(Model model) {
        model.addAttribute("especialidades", especialidadService.listar());
        return "paciente/reservar";
    }

    @PostMapping("/reservar")
    public String reservar(@SessionAttribute("usuario") UsuarioSesion u, @RequestParam Integer idEspecialidad,
                           @RequestParam Integer idProfesional, @RequestParam Integer idHorario,
                           RedirectAttributes ra) {
        try {
            citaService.reservar(u.id(), idEspecialidad, idProfesional, idHorario);
            ra.addFlashAttribute("mensaje", "¡Cita reservada con éxito! Recuerda acudir a caja para pagar tu consulta.");
            return "redirect:/paciente/citas";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/paciente/reservar";
        }
    }

    @PostMapping("/citas/{id}/cancelar")
    public String cancelar(@SessionAttribute("usuario") UsuarioSesion u, @PathVariable Integer id,
                           RedirectAttributes ra) {
        try {
            citaService.cancelar(id, u);
            ra.addFlashAttribute("mensaje", "La cita fue cancelada y el horario quedó liberado.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/paciente/citas";
    }
}
