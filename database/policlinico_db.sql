-- =====================================================================
--  Base de datos: policlinico_db (MySQL 8+)
--  Policlínico Parroquial Hermano Andrés - Sistema de reservas de citas
--  Basada en el diagrama entidad-relación (modelo físico) del proyecto
-- =====================================================================
CREATE DATABASE policlinico_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE policlinico_db;

-- ---------------------------------------------------------------------
-- ESPECIALIDAD
-- ---------------------------------------------------------------------
CREATE TABLE especialidad (
    idEspecialidad INT           NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(100)  NOT NULL,
    tarifa         DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_especialidad PRIMARY KEY (idEspecialidad),
    CONSTRAINT uk_especialidad_nombre UNIQUE (nombre),
    CONSTRAINT ck_especialidad_tarifa CHECK (tarifa >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- PERSONAL_POLICLINICO (supertipo de profesional, cajero y coordinador)
-- ---------------------------------------------------------------------
CREATE TABLE personal_policlinico (
    idPersonal         INT          NOT NULL AUTO_INCREMENT,
    nombres            VARCHAR(100) NOT NULL,
    apellidos          VARCHAR(100) NOT NULL,
    documentoIdentidad VARCHAR(20)  NOT NULL,
    correoElectronico  VARCHAR(150) NOT NULL,
    contrasena         VARCHAR(255) NOT NULL,   -- hash bcrypt
    CONSTRAINT pk_personal PRIMARY KEY (idPersonal),
    CONSTRAINT uk_personal_documento UNIQUE (documentoIdentidad)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- PROFESIONAL_SALUD (subtipo)  "asigna" especialidad / "corresponde a" personal
-- ---------------------------------------------------------------------
CREATE TABLE profesional_salud (
    idProfesional  INT NOT NULL,
    idEspecialidad INT NOT NULL,
    CONSTRAINT pk_profesional PRIMARY KEY (idProfesional),
    CONSTRAINT fk_profesional_personal FOREIGN KEY (idProfesional)
        REFERENCES personal_policlinico (idPersonal) ON DELETE CASCADE,
    CONSTRAINT fk_profesional_especialidad FOREIGN KEY (idEspecialidad)
        REFERENCES especialidad (idEspecialidad)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- CAJERO (subtipo)
-- ---------------------------------------------------------------------
CREATE TABLE cajero (
    idCajero INT NOT NULL,
    CONSTRAINT pk_cajero PRIMARY KEY (idCajero),
    CONSTRAINT fk_cajero_personal FOREIGN KEY (idCajero)
        REFERENCES personal_policlinico (idPersonal) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- COORDINADOR (subtipo)
-- ---------------------------------------------------------------------
CREATE TABLE coordinador (
    idCoordinador INT NOT NULL,
    CONSTRAINT pk_coordinador PRIMARY KEY (idCoordinador),
    CONSTRAINT fk_coordinador_personal FOREIGN KEY (idCoordinador)
        REFERENCES personal_policlinico (idPersonal) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- PACIENTE
-- ---------------------------------------------------------------------
CREATE TABLE paciente (
    idPaciente         INT          NOT NULL AUTO_INCREMENT,
    nombres            VARCHAR(100) NOT NULL,
    apellidos          VARCHAR(100) NOT NULL,
    documentoIdentidad VARCHAR(20)  NOT NULL,
    correoElectronico  VARCHAR(150) NOT NULL,
    numeroCelular      VARCHAR(15)  NOT NULL,
    contrasena         VARCHAR(255) NOT NULL,   -- hash bcrypt
    CONSTRAINT pk_paciente PRIMARY KEY (idPaciente),
    CONSTRAINT uk_paciente_documento UNIQUE (documentoIdentidad)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- HORARIO  "gestiona" el profesional.  UK compuesta (idProfesional, fecha, hora)
-- ---------------------------------------------------------------------
CREATE TABLE horario (
    idHorario     INT         NOT NULL AUTO_INCREMENT,
    fecha         DATE        NOT NULL,
    hora          TIME        NOT NULL,
    estado        VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    idProfesional INT         NOT NULL,
    CONSTRAINT pk_horario PRIMARY KEY (idHorario),
    CONSTRAINT uk_horario UNIQUE (idProfesional, fecha, hora),
    CONSTRAINT ck_horario_estado CHECK (estado IN ('DISPONIBLE','RESERVADO')),
    CONSTRAINT fk_horario_profesional FOREIGN KEY (idProfesional)
        REFERENCES profesional_salud (idProfesional)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- CITA_MEDICA
-- ---------------------------------------------------------------------
CREATE TABLE cita_medica (
    idCita           INT         NOT NULL AUTO_INCREMENT,
    estado           VARCHAR(20) NOT NULL DEFAULT 'RESERVADA',
    registroAtencion TEXT        NULL,
    idPaciente       INT         NOT NULL,
    idProfesional    INT         NOT NULL,
    idEspecialidad   INT         NOT NULL,
    idHorario        INT         NOT NULL,
    CONSTRAINT pk_cita PRIMARY KEY (idCita),
    CONSTRAINT ck_cita_estado CHECK (estado IN ('RESERVADA','FINALIZADA','CANCELADA')),
    CONSTRAINT fk_cita_paciente FOREIGN KEY (idPaciente)
        REFERENCES paciente (idPaciente),
    CONSTRAINT fk_cita_profesional FOREIGN KEY (idProfesional)
        REFERENCES profesional_salud (idProfesional),
    CONSTRAINT fk_cita_especialidad FOREIGN KEY (idEspecialidad)
        REFERENCES especialidad (idEspecialidad),
    CONSTRAINT fk_cita_horario FOREIGN KEY (idHorario)
        REFERENCES horario (idHorario)
) ENGINE=InnoDB;

CREATE INDEX ix_cita_paciente    ON cita_medica (idPaciente);
CREATE INDEX ix_cita_profesional ON cita_medica (idProfesional);
CREATE INDEX ix_cita_horario     ON cita_medica (idHorario);

-- ---------------------------------------------------------------------
-- PAGO  "genera" a partir de la cita / "registra" el cajero
-- ---------------------------------------------------------------------
CREATE TABLE pago (
    idPago    INT           NOT NULL AUTO_INCREMENT,
    monto     DECIMAL(10,2) NOT NULL,
    formaPago VARCHAR(30)   NOT NULL,
    idCita    INT           NOT NULL,
    idCajero  INT           NOT NULL,
    CONSTRAINT pk_pago PRIMARY KEY (idPago),
    CONSTRAINT uk_pago_cita UNIQUE (idCita),          -- un pago por cita
    CONSTRAINT ck_pago_forma CHECK (formaPago IN ('EFECTIVO','TARJETA')),
    CONSTRAINT fk_pago_cita FOREIGN KEY (idCita)
        REFERENCES cita_medica (idCita),
    CONSTRAINT fk_pago_cajero FOREIGN KEY (idCajero)
        REFERENCES cajero (idCajero)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- COMPROBANTE_PAGO  "genera" a partir del pago
-- ---------------------------------------------------------------------
CREATE TABLE comprobante_pago (
    idComprobante INT           NOT NULL AUTO_INCREMENT,
    datosPaciente VARCHAR(255)  NOT NULL,
    datosCita     VARCHAR(255)  NOT NULL,
    monto         DECIMAL(10,2) NOT NULL,
    formaPago     VARCHAR(30)   NOT NULL,
    idPago        INT           NOT NULL,
    CONSTRAINT pk_comprobante PRIMARY KEY (idComprobante),
    CONSTRAINT uk_comprobante_pago UNIQUE (idPago),
    CONSTRAINT fk_comprobante_pago FOREIGN KEY (idPago)
        REFERENCES pago (idPago)
) ENGINE=InnoDB;

-- =====================================================================
--  DATOS INICIALES
--  Las cuentas de prueba (coordinador, cajero, profesionales, paciente)
--  las crea la aplicación al iniciar por primera vez (DataSeeder) para
--  guardar las contraseñas ya cifradas con bcrypt.
-- =====================================================================
INSERT INTO especialidad (nombre, tarifa) VALUES
    ('Medicina General', 35.00),
    ('Pediatría',        45.00),
    ('Ginecología',      60.00),
    ('Cardiología',      80.00),
    ('Odontología',      40.00),
    ('Traumatología',    70.00),
    ('Dermatología',     55.00),
    ('Oftalmología',     50.00),
    ('Psicología',       50.00);