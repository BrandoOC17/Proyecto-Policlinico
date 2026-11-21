package pe.utp.policlinico.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import pe.utp.policlinico.config.Roles;
import pe.utp.policlinico.dto.RegistroPacienteForm;
import pe.utp.policlinico.dto.UsuarioSesion;
import pe.utp.policlinico.exception.NegocioException;
import pe.utp.policlinico.service.AuthService;
import pe.utp.policlinico.service.PacienteService;

@Controller
public class AuthController {

    private final AuthService authService;
    private final PacienteService pacienteService;

    public AuthController(AuthService authService, PacienteService pacienteService) {
        this.authService = authService;
        this.pacienteService = pacienteService;
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session) {
        UsuarioSesion u = (UsuarioSesion) session.getAttribute("usuario");
        return u != null ? "redirect:" + inicioDe(u) : "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String documento, @RequestParam String password,
                        HttpServletRequest request, Model model) {
        try {
            UsuarioSesion u = authService.autenticar(documento, password);
            request.getSession(true);
            request.changeSessionId(); // evita fijación de sesión
            request.getSession().setAttribute("usuario", u);
            return "redirect:" + inicioDe(u);
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("documento", documento);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/registro")
    public String registroForm() {
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("f") RegistroPacienteForm f, BindingResult errores,
                            Model model, RedirectAttributes ra) {
        if (errores.hasErrors()) {
            model.addAttribute("error", errores.getAllErrors().get(0).getDefaultMessage());
            return "registro";
        }
        try {
            pacienteService.registrar(f);
            ra.addFlashAttribute("mensaje", "Cuenta creada correctamente. Ya puedes iniciar sesión.");
            return "redirect:/login";
        } catch (NegocioException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }

    private String inicioDe(UsuarioSesion u) {
        return switch (u.rol()) {
            case Roles.PACIENTE -> "/paciente/citas";
            case Roles.PROFESIONAL -> "/profesional/citas";
            case Roles.COORDINADOR -> "/coordinador/panel";
            default -> "/cajero/pendientes";
        };
    }
}
