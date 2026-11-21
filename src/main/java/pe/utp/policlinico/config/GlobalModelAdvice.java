package pe.utp.policlinico.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pe.utp.policlinico.dto.UsuarioSesion;

/** Pone el usuario de la sesión en todas las vistas como ${usuarioActual}. */
@ControllerAdvice
public class GlobalModelAdvice {
    @ModelAttribute("usuarioActual")
    public UsuarioSesion usuarioActual(HttpServletRequest request) {
        HttpSession s = request.getSession(false);
        return s == null ? null : (UsuarioSesion) s.getAttribute("usuario");
    }
}
