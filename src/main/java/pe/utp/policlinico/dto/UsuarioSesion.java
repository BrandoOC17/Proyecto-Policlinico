package pe.utp.policlinico.dto;

/** Usuario autenticado que se guarda en la sesión HTTP. */
public record UsuarioSesion(int id, String nombres, String apellidos, String documento, String rol) {
    public String nombreCompleto() { return nombres + " " + apellidos; }
}
