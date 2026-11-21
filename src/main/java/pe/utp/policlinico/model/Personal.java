package pe.utp.policlinico.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Tabla personal_policlinico. Las tablas profesional_salud, cajero y coordinador
 * son subtipos de esta entidad:
 *  - profesional_salud se mapea con @JoinTable (idProfesional -> idEspecialidad),
 *    por eso "especialidad" solo tiene valor cuando el personal es profesional de salud.
 *  - cajero y coordinador (solo guardan el id) se manejan en PersonalRepository.
 */
@Entity
@Table(name = "personal_policlinico")
public class Personal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPersonal")
    private Integer id;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "documentoIdentidad", nullable = false, length = 20)
    private String documento;

    @Column(name = "correoElectronico", nullable = false, length = 150)
    private String correo;

    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "profesional_salud",
            joinColumns = @JoinColumn(name = "idProfesional"),
            inverseJoinColumns = @JoinColumn(name = "idEspecialidad"))
    private Especialidad especialidad;

    public Personal() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
