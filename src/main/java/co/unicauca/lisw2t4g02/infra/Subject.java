package co.unicauca.lisw2t4g02.infra;

import java.util.ArrayList;
import java.util.List;

/**
 * @class Subject
 * @brief Clase base del patrón de diseño Observer: mantiene la lista de
 * observadores y los notifica ante cambios de estado.
 *
 * <b>Nota de refactorización:</b> la versión original declaraba un método
 * {@code public void Subject()} (con el mismo nombre de la clase pero con
 * tipo de retorno) que nunca se ejecutaba como constructor real; se
 * reemplaza por un constructor explícito que inicializa la lista de
 * observadores, evitando además la comprobación de nulos en
 * {@link #addObserver(Observer)}.
 *
 * @author Grupo LISW2 T4 G02
 */
public abstract class Subject {

    /** Observadores suscritos a los cambios de este sujeto. */
    private final List<Observer> observers = new ArrayList<>();

    /**
     * @brief Agrega un observador que será notificado ante cambios.
     * @param obs observador a registrar.
     */
    public void addObserver(Observer obs) {
        observers.add(obs);
    }

    /**
     * @brief Notifica a todos los observadores registrados que hubo un
     * cambio en el modelo.
     */
    public void notifyAllObserves() {
        for (Observer each : observers) {
            each.update(this);
        }
    }
}
