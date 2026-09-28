package co.edu.unicauca.lisw2t2go2.domain;

/**
 * RF-03: El sistema debera administrar diferentes roles.
 *
 * Se modela como enum (y no como String suelto) para que el compilador
 * garantice que solo existan los roles definidos por el negocio y para
 * evitar "magic strings" dispersos por el codigo.
 *
 * Cada constante guarda ademas su "dbValue": el texto EXACTO con el que
 * ese rol esta escrito en la tabla 'usuario' de la base de datos SQLite
 * existente (por ejemplo "Autor de preguntas", con espacios y mayuscula
 * inicial, en vez de "AUTOR_PREGUNTAS"). Esto permite que el codigo Java
 * use nombres de enum comodos e inequivocos, sin obligar a modificar
 * los datos ya guardados en la base.
 *
 * @author David Santiago Cruz Varón, Juan Felipe Gallardo Orozco
 */
public enum Role {
    ADMINISTRADOR("Administrador"),
    AUTOR_PREGUNTAS("Autor de preguntas"),
    REVISOR("Revisor"),
    DOCENTE("Docente"),
    ESTUDIANTE("Estudiante");

    private final String dbValue;

    Role(String dbValue) {
        this.dbValue = dbValue;
    }

    /**
     * Texto exacto que se guarda/lee en la columna usu_rol.
     */
    public String getDbValue() {
        return dbValue;
    }

    /**
     * Reconstruye el enum a partir del texto guardado en la base de datos.
     * La comparacion ignora mayusculas/minusculas por seguridad, pero el
     * valor de referencia sigue siendo dbValue.
     *
     * @throws IllegalArgumentException si el texto no corresponde a ningun rol conocido
     */
    public static Role fromDbValue(String dbValue) {
        for (Role role : values()) {
            if (role.dbValue.equalsIgnoreCase(dbValue)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Rol desconocido en la base de datos: '" + dbValue + "'");
    }

    @Override
    public String toString() {
        return dbValue;
    }
}
