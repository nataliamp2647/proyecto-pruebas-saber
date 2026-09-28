package co.edu.unicauca.lisw2t5g02.domain.question;

import co.edu.unicauca.lisw2t5g02.domain.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    @Test
    void notificarAsignacion_deberiaInformarConfiguracionSMTPFaltante() {
        // La prueba es unitaria: no intenta conectarse a un servidor SMTP real.
        // Si SMTP_HOST, SMTP_USER o SMTP_PASSWORD no estan configurados,
        // el servicio debe detenerse antes de intentar enviar el correo.
        NotificationService service = new NotificationService();
        Question question = new Question();
        User user = new User();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.notificarAsignacion(question, user));

        assertTrue(exception.getMessage().contains("Falta configurar la variable de entorno"));
    }
}
