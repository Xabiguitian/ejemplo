-- ----------------------------------------------------------------------------
-- TechFic Survey Model (Práctica ISD 2025-2026)
-- ----------------------------------------------------------------------------

DROP TABLE IF EXISTS Respuesta;
DROP TABLE IF EXISTS Encuesta;

CREATE TABLE Encuesta (
                          encuestaId BIGINT NOT NULL AUTO_INCREMENT,
                          pregunta VARCHAR(255) NOT NULL,
                          fechaCreacion DATETIME NOT NULL,
                          fechaFin DATETIME NOT NULL,
                          respuestasPositivas BIGINT NOT NULL DEFAULT 0,
                          respuestasNegativas BIGINT NOT NULL DEFAULT 0,
                          cancelada BOOLEAN NOT NULL DEFAULT 0,
                          CONSTRAINT EncuestaPK PRIMARY KEY (encuestaId)
) ENGINE = InnoDB;

CREATE TABLE Respuesta (
                           respuestaId BIGINT NOT NULL AUTO_INCREMENT,
                           encuestaId BIGINT NOT NULL,
                           emailEmpleado VARCHAR(255) NOT NULL,
                           afirmativa BOOLEAN NOT NULL,
                           fechaRespuesta DATETIME NOT NULL,
                           CONSTRAINT RespuestaPK PRIMARY KEY (respuestaId),
                           CONSTRAINT RespuestaEncuestaFK FOREIGN KEY (encuestaId)
                               REFERENCES Encuesta(encuestaId) ON DELETE CASCADE
) ENGINE = InnoDB;
