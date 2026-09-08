package co.unicauca.lisw2t4g02;

import co.unicauca.lisw2t4g02.access.QuestionImplRepository;
import co.unicauca.lisw2t4g02.domain.QuestionRepository;
import co.unicauca.lisw2t4g02.domain.QuestionService;
import co.unicauca.lisw2t4g02.presentation.GUIObserver1;
import co.unicauca.lisw2t4g02.presentation.GUIObserver2;
import co.unicauca.lisw2t4g02.presentation.GUIQuestions;

import javax.swing.UIManager;
import java.awt.EventQueue;

/**
 * @class Application
 * @brief Punto de composición y entrada de la aplicación.
 *
 * No pertenece a una capa funcional; únicamente ensambla las cuatro capas.
 *
 * @author Grupo LISW2 T4 G02
 */
public final class Application {
    private Application() { }

    /** @param args argumentos de línea de comandos. */
    public static void main(String[] args) {
        aplicarLookAndFeelNimbus();
        EventQueue.invokeLater(() -> {
            QuestionRepository repositorio = new QuestionImplRepository();
            QuestionService servicio = new QuestionService(repositorio);

            GUIQuestions ventana = new GUIQuestions(servicio);
            GUIObserver1 observer1 = new GUIObserver1(servicio);
            GUIObserver2 observer2 = new GUIObserver2(servicio);

            servicio.addObserver(observer1);
            servicio.addObserver(observer2);

            ventana.setLocation(60, 60);
            observer1.setLocation(620, 60);
            observer2.setLocation(620, 360);

            ventana.setVisible(true);
            observer1.setVisible(true);
            observer2.setVisible(true);
        });
    }

    private static void aplicarLookAndFeelNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) { }
    }
}
