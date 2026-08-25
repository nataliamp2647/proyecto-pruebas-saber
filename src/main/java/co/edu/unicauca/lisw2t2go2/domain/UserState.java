package co.edu.unicauca.lisw2t2go2.domain;

/**
 * Estado del usuario dentro del sistema.
 *
 * Igual que {@link Role}, guarda el texto exacto ("activo"/"inactivo",
 * en minuscula) tal como esta escrito en la base de datos SQLite
 * existente, para no tener que migrar los datos ya guardados.
 *
 * @author Claude
 */
public enum UserState {
    ACTIVO("activo"),
    INACTIVO("inactivo");

    private final String dbValue;

    UserState(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static UserState fromDbValue(String dbValue) {
        for (UserState state : values()) {
            if (state.dbValue.equalsIgnoreCase(dbValue)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Estado desconocido en la base de datos: '" + dbValue + "'");
    }

    @Override
    public String toString() {
        return dbValue;
    }
}
