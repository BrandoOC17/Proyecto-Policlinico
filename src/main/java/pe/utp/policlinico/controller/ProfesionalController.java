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

@Controller
@RequestMapping("/profesional")
public class ProfesionalController {

    private final CitaService citaService;

    public ProfesionalController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping("/citas")
    public String citasDelTurno(@SessionAttribute("usuario") UsuarioSesion u, Model model) {
        model.addAttribute("citas", citaService.citasDelTurno(u.id()));
        return "profesional/citasProfesional";
    }

    @GetMapping("/citas/{id}/atender")
    public String atenderForm(@SessionAttribute("usuario") UsuarioSesion u, @PathVariable Integer id,
            Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("cita", citaService.citaDelProfesional(id, u.id()));
            return "profesional/atender";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/profesional/citas";
        }
    }

    @PostMapping("/citas/{id}/finalizar")
    public String finalizar(@SessionAttribute("usuario") UsuarioSesion u, @PathVariable Integer id,
            @RequestParam String registroAtencion, RedirectAttributes ra) {
        try {
            citaService.finalizar(id, u.id(), registroAtencion);
            ra.addFlashAttribute("mensaje", "Atención registrada. La cita quedó FINALIZADA.");
            return "redirect:/profesional/citas";
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/profesional/citas/" + id + "/atender";
        }
    }
}
