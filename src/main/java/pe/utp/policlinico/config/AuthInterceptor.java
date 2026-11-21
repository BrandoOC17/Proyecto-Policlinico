package pe.utp.policlinico.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;
import pe.utp.policlinico.dto.UsuarioSesion;

/** Restringe el acceso a cada zona del sistema según el rol (RNF02). */
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession session = req.getSession(false);
        UsuarioSesion u = session == null ? null : (UsuarioSesion) session.getAttribute("usuario");
        if (u == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return false;
        }
        String requerido = rolRequerido(req.getRequestURI());
        if (requerido != null && !requerido.equals(u.rol())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "No tienes permiso para acceder a esta sección.");
            return false;
        }
        return true;
    }

    private String rolRequerido(String uri) {
        if (uri.startsWith("/paciente")) return Roles.PACIENTE;
        if (uri.startsWith("/profesional")) return Roles.PROFESIONAL;
        if (uri.startsWith("/coordinador")) return Roles.COORDINADOR;
        if (uri.startsWith("/cajero")) return Roles.CAJERO;
        return null; // /api/**: basta con haber iniciado sesión
    }
}
