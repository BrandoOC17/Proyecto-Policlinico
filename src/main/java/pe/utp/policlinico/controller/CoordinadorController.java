package pe.utp.policlinico.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import pe.utp.policlinico.dto.HorarioForm;
import pe.utp.policlinico.dto.PersonalForm;
import pe.utp.policlinico.dto.UsuarioSesion;
import pe.utp.policlinico.exception.NegocioException;
import pe.utp.policlinico.service.CitaService;
import pe.utp.policlinico.service.EspecialidadService;
import pe.utp.policlinico.service.HorarioService;
import pe.utp.policlinico.service.PagoService;
import pe.utp.policlinico.service.PersonalService;

@Controller
@RequestMapping("/coordinador")
public class CoordinadorController {

    private final CitaService citaService;
    private final PagoService pagoService;
    private final PersonalService personalService;
    private final EspecialidadService especialidadService;
    private final HorarioService horarioService;

    public CoordinadorController(CitaService citaService, PagoService pagoService,
                                 PersonalService personalService, EspecialidadService especialidadService,
                                 HorarioService horarioService) {
        this.citaService = citaService;
        this.pagoService = pagoService;
        this.personalService = personalService;
        this.especialidadService = especialidadService;
        this.horarioService = horarioService;
    }

    // ---- CU08 Panel de control ----
    @GetMapping("/panel")
    public String panel(Model model) {
        model.addAttribute("porEstado", citaService.contarPorEstado());
        model.addAttribute("recaudado", pagoService.totalRecaudado());
        model.addAttribute("citas", citaService.todas());
        return "coordinador/panel";
    }

    // ---- CU06 Cancelar cita desde el panel ----
    @PostMapping("/citas/{id}/cancelar")
    public String cancelar(@SessionAttribute("usuario") UsuarioSesion u, @PathVariable Integer id,
                           RedirectAttributes ra) {
        try {
            citaService.cancelar(id, u);
            ra.addFlashAttribute("mensaje", "Cita cancelada y horario liberado.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/panel";
    }

    // ---- CU03 Registrar personal ----
    @GetMapping("/personal")
    public String personalForm(Model model) {
        model.addAttribute("especialidades", especialidadService.listar());
        return "coordinador/personal";
    }

    @PostMapping("/personal")
    public String registrarPersonal(@Valid @ModelAttribute("f") PersonalForm f, BindingResult errores,
                                    Model model, RedirectAttributes ra) {
        try {
            if (errores.hasErrors()) {
                throw new NegocioException(errores.getAllErrors().get(0).getDefaultMessage());
            }
            personalService.registrar(f);
            ra.addFlashAttribute("mensaje", "Personal registrado correctamente.");
            return "redirect:/coordinador/personal";
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("especialidades", especialidadService.listar());
            return "coordinador/personal";
        }
    }

    // ---- CU07 Especialidades y profesionales ----
    @GetMapping("/catalogo")
    public String catalogo(Model model) {
        model.addAttribute("especialidades", especialidadService.listar());
        model.addAttribute("profesionales", personalService.listarProfesionales());
        return "coordinador/catalogo";
    }

    @PostMapping("/especialidades")
    public String guardarEspecialidad(@RequestParam(required = false) Integer id, @RequestParam String nombre,
                                      @RequestParam String tarifa, RedirectAttributes ra) {
        try {
            especialidadService.guardar(id, nombre, tarifa);
            ra.addFlashAttribute("mensaje", "Especialidad guardada.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/catalogo";
    }

    @PostMapping("/especialidades/{id}/eliminar")
    public String eliminarEspecialidad(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            especialidadService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Especialidad eliminada.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/catalogo";
    }

    @PostMapping("/profesionales/{id}/especialidad")
    public String cambiarEspecialidad(@PathVariable Integer id, @RequestParam Integer idEspecialidad,
                                      RedirectAttributes ra) {
        try {
            personalService.cambiarEspecialidad(id, idEspecialidad);
            ra.addFlashAttribute("mensaje", "Especialidad del profesional actualizada.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/catalogo";
    }

    // ---- CU07 Horarios ----
    @GetMapping("/horarios")
    public String horarios(Model model) {
        model.addAttribute("profesionales", personalService.listarProfesionales());
        model.addAttribute("horarios", horarioService.programados());
        return "coordinador/horarios";
    }

    @PostMapping("/horarios")
    public String generarHorarios(@Valid @ModelAttribute("form") HorarioForm form, BindingResult errores,
                                  RedirectAttributes ra) {
        try {
            if (errores.hasErrors()) {
                throw new NegocioException(errores.getAllErrors().get(0).getDefaultMessage());
            }
            int n = horarioService.generar(form.getIdProfesional(), form.getFecha(), form.getHoraInicio(),
                    form.getHoraFin(), form.getMinutos());
            ra.addFlashAttribute("mensaje", n + " horario(s) creado(s).");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/horarios";
    }

    @PostMapping("/horarios/{id}/eliminar")
    public String eliminarHorario(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            horarioService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Horario eliminado.");
        } catch (NegocioException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/horarios";
    }
}