package co.unicauca.lisw2t4g02.infra;

/**
 * @interface Observer
 * @brief Contrato del patrón de diseño Observer para objetos que desean
 * ser notificados de cambios en un {@link Subject}.
 *
 * @author Grupo LISW2 T4 G02
 */
public interface Observer {

    /**
     * @brief Método invocado por el {@link Subject} cuando ocurre un
     * cambio de estado que debe ser notificado.
     * @param o objeto que originó la notificación (normalmente, el
     * propio {@link Subject}).
     */
    public void update(Object o);
}
