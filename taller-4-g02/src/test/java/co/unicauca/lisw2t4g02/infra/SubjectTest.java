package co.unicauca.lisw2t4g02.infra;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubjectTest {

    /** Subject concreto mínimo, solo para poder instanciar la clase abstracta en el test. */
    private static class SubjectDePrueba extends Subject { }

    private static class ObserverEspia implements Observer {
        final List<Object> llamadasRecibidas = new ArrayList<>();

        @Override
        public void update(Object o) {
            llamadasRecibidas.add(o);
        }
    }

    @Test
    void notifyAllObserves_sinObservadoresRegistrados_noLanzaExcepcion() {
        Subject sujeto = new SubjectDePrueba();

        assertDoesNotThrow(sujeto::notifyAllObserves);
    }

    @Test
    void notifyAllObserves_llamaUpdateEnElUnicoObservadorRegistrado() {
        Subject sujeto = new SubjectDePrueba();
        ObserverEspia observer = new ObserverEspia();
        sujeto.addObserver(observer);

        sujeto.notifyAllObserves();

        assertEquals(1, observer.llamadasRecibidas.size());
    }

    @Test
    void notifyAllObserves_llamaUpdateEnCadaUnoDeVariosObservadoresRegistrados() {
        Subject sujeto = new SubjectDePrueba();
        ObserverEspia observer1 = new ObserverEspia();
        ObserverEspia observer2 = new ObserverEspia();
        ObserverEspia observer3 = new ObserverEspia();
        sujeto.addObserver(observer1);
        sujeto.addObserver(observer2);
        sujeto.addObserver(observer3);

        sujeto.notifyAllObserves();

        assertEquals(1, observer1.llamadasRecibidas.size());
        assertEquals(1, observer2.llamadasRecibidas.size());
        assertEquals(1, observer3.llamadasRecibidas.size());
    }

    @Test
    void notifyAllObserves_pasaElPropioSujetoComoArgumentoAlObserver() {
        Subject sujeto = new SubjectDePrueba();
        ObserverEspia observer = new ObserverEspia();
        sujeto.addObserver(observer);

        sujeto.notifyAllObserves();

        assertSame(sujeto, observer.llamadasRecibidas.get(0));
    }
}
