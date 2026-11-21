package pe.utp.policlinico.config;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import pe.utp.policlinico.dao.PersonalDao;
import pe.utp.policlinico.dto.PersonalForm;
import pe.utp.policlinico.dto.RegistroPacienteForm;
import pe.utp.policlinico.model.Especialidad;
import pe.utp.policlinico.model.Personal;
import pe.utp.policlinico.service.EspecialidadService;
import pe.utp.policlinico.service.HorarioService;
import pe.utp.policlinico.service.PacienteService;
import pe.utp.policlinico.service.PersonalService;

@Component
public class DataSeeder implements CommandLineRunner {

    // DNI de los 18 profesionales iniciales (2 por especialidad, en este orden).
    // La posición decide el turno: posición par = mañana, posición impar = tarde.
    // Se usa tanto al crearlos como al renovar sus horarios.
    private static final String[] DNI_MEDICOS = {
        "46317285",
        "57589033",
        "45218764",
        "70325981",
        "48736215",
        "61874029",
        "73954186",
        "44189370",
        "76218493",
        "40927158",
        "71563820",
        "46082971",
        "74391056",
        "42760819",
        "75128364",
        "43871592",
        "77046215",
        "41539087"
    };

    private final PersonalDao personalDao;
    private final EspecialidadService especialidadService;
    private final PersonalService personalService;
    private final HorarioService horarioService;
    private final PacienteService pacienteService;

    public DataSeeder(PersonalDao personalDao, EspecialidadService especialidadService,
                      PersonalService personalService, HorarioService horarioService,
                      PacienteService pacienteService) {
        this.personalDao = personalDao;
        this.especialidadService = especialidadService;
        this.personalService = personalService;
        this.horarioService = horarioService;
        this.pacienteService = pacienteService;
    }

    @Override
    public void run(String... args) {
        if (personalDao.count() > 0) {
            renovarHorarios();   // ya hay datos: solo genera los horarios de los próximos 5 días
            return;
        }
        List<Especialidad> especialidades = especialidadService.listar();
        if (especialidades.isEmpty()) {
            return;
        }

        personalService.registrarCoordinador("Ernesto", "Medina Rojas", "10008901",
                "coordinador@policlinico.pe", "Coordinador2026");
        personalService.registrar(personal("Luis", "Mendoza Diaz", "10000572", "cajero@policlinico.pe",
                "Cajero123", Roles.CAJERO, null));

        // 2 profesionales por especialidad (contraseña de todos: Medico123)
        // Los nombres de la especialidad deben ser EXACTOS a los de la base de datos
        Integer idMed = idDe(especialidades, "Medicina General");
        Integer idPed = idDe(especialidades, "Pediatría");
        Integer idGin = idDe(especialidades, "Ginecología");
        Integer idCar = idDe(especialidades, "Cardiología");
        Integer idOdo = idDe(especialidades, "Odontología");
        Integer idTra = idDe(especialidades, "Traumatología");
        Integer idDer = idDe(especialidades, "Dermatología");
        Integer idOft = idDe(especialidades, "Oftalmología");
        Integer idPsi = idDe(especialidades, "Psicología");

        crearProfesional("Ana",      "Torres Vega",         DNI_MEDICOS[0], idMed, true);
        crearProfesional("Jorge",    "Ramos Cruz",          DNI_MEDICOS[1], idMed, false);
        crearProfesional("Rosa",     "Flores Delgado",      DNI_MEDICOS[2], idPed, true);
        crearProfesional("Carlos",   "Castillo Arce",       DNI_MEDICOS[3], idPed, false);
        crearProfesional("Lucía",    "Paredes Lozano",      DNI_MEDICOS[4], idGin, true);
        crearProfesional("Miguel",   "Huamán Salazar",      DNI_MEDICOS[5], idGin, false);
        crearProfesional("Elena",    "Medina Herrera",      DNI_MEDICOS[6], idCar, true);
        crearProfesional("Pedro",    "Chávez Ríos",         DNI_MEDICOS[7], idCar, false);
        crearProfesional("Sofía",    "Vargas Campos",       DNI_MEDICOS[8], idOdo, true);
        crearProfesional("Raúl",     "Aguilar Ortega",      DNI_MEDICOS[9], idOdo, false);
        crearProfesional("Patricia", "Silva Navarro",       DNI_MEDICOS[10], idTra, true);
        crearProfesional("Javier",   "Peña Cabrera",        DNI_MEDICOS[11], idTra, false);
        crearProfesional("Gabriela", "Soto Guerrero",       DNI_MEDICOS[12], idDer, true);
        crearProfesional("Diego",    "Ledesma Valdivia",    DNI_MEDICOS[13], idDer, false);
        crearProfesional("Valeria",  "Zevallos Bustamante", DNI_MEDICOS[14], idOft, true);
        crearProfesional("Andrés",   "Carrillo Espinoza",   DNI_MEDICOS[15], idOft, false);
        crearProfesional("Mónica",   "Gamarra Montoya",     DNI_MEDICOS[16], idPsi, true);
        crearProfesional("Fernando", "Palacios Benites",    DNI_MEDICOS[17], idPsi, false);

        RegistroPacienteForm p = new RegistroPacienteForm();
        p.setNombres("Paciente");
        p.setApellidos("Demo Prueba");
        p.setDocumento("70000001");
        p.setCorreo("paciente@correo.pe");
        p.setCelular("987654321");
        p.setPassword("Paciente123");
        p.setPassword2("Paciente123");
        pacienteService.registrar(p);
    }

    private Integer idDe(List<Especialidad> lista, String nombre) {
        return lista.stream().filter(e -> e.getNombre().equals(nombre)).findFirst()
                .orElse(lista.get(0)).getId();
    }

    // Crea un profesional y sus horarios de los próximos 5 días (turno mañana o tarde)
    private void crearProfesional(String nombres, String apellidos, String doc, Integer idEspecialidad,
                                  boolean turnoManana) {
        Integer id = personalService.registrar(personal(nombres, apellidos, doc, doc + "@policlinico.pe",
                "Medico123", Roles.PROFESIONAL, idEspecialidad));
        generarHorarios(id, turnoManana);
    }

    // Genera horarios de mañana hasta dentro de 5 días. No duplica los que ya existen.
    private void generarHorarios(Integer idProfesional, boolean turnoManana) {
        LocalTime inicio = turnoManana ? LocalTime.of(8, 0) : LocalTime.of(14, 0);
        LocalTime fin = turnoManana ? LocalTime.of(12, 0) : LocalTime.of(18, 0);
        for (int d = 1; d <= 5; d++) {
            horarioService.generar(idProfesional, LocalDate.now().plusDays(d), inicio, fin, 30);
        }
    }

    // Renueva los horarios de TODOS los profesionales de la BD (también los que agregue el coordinador).
    // Los 18 iniciales usan su posición en DNI_MEDICOS para el turno; los nuevos, el id (par = mañana).
    private void renovarHorarios() {
        List<String> dnis = Arrays.asList(DNI_MEDICOS);
        for (Personal prof : personalService.listarProfesionales()) {
            int pos = dnis.indexOf(prof.getDocumento());
            boolean turnoManana = (pos >= 0) ? (pos % 2 == 0) : (prof.getId() % 2 == 0);
            generarHorarios(prof.getId(), turnoManana);
        }
    }

    private PersonalForm personal(String nombres, String apellidos, String doc, String correo, String pass,
                                  String rol, Integer idEspecialidad) {
        PersonalForm f = new PersonalForm();
        f.setNombres(nombres);
        f.setApellidos(apellidos);
        f.setDocumento(doc);
        f.setCorreo(correo);
        f.setPassword(pass);
        f.setRol(rol);
        f.setIdEspecialidad(idEspecialidad);
        return f;
    }
}