package pe.utp.policlinico.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Formulario de registro de paciente (CU01). */
public class RegistroPacienteForm {

    @NotBlank(message = "El campo nombres es obligatorio.")
    private String nombres;

    @NotBlank(message = "El campo apellidos es obligatorio.")
    private String apellidos;

    @Pattern(regexp = "^[A-Za-z0-9]{8,12}$",
            message = "El documento de identidad debe tener entre 8 y 12 caracteres alfanuméricos.")
    private String documento;

    @NotBlank(message = "El correo electrónico es obligatorio.")
    @Email(message = "El correo electrónico no es válido.")
    private String correo;

    @Pattern(regexp = "^9\\d{8}$", message = "El celular debe tener 9 dígitos y empezar con 9.")
    private String celular;

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password;

    private String password2;

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPassword2() { return password2; }
    public void setPassword2(String password2) { this.password2 = password2; }
}
