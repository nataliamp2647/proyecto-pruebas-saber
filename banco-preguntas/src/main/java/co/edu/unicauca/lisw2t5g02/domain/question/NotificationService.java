package co.edu.unicauca.lisw2t5g02.domain.question;

import co.edu.unicauca.lisw2t5g02.domain.user.User;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

/** Sends reviewer notifications through configured SMTP and records successful deliveries. */
public class NotificationService {
    public void notificarAsignacion(Question q, User user) {
        String host=required("SMTP_HOST"), username=required("SMTP_USER"), password=required("SMTP_PASSWORD");
        String from=System.getenv().getOrDefault("SMTP_FROM",username);
        String port=System.getenv().getOrDefault("SMTP_PORT","587");
        Properties properties=new Properties();
        properties.put("mail.smtp.auth","true"); properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host",host); properties.put("mail.smtp.port",port);
        properties.put("mail.smtp.ssl.trust",host);
        Session session=Session.getInstance(properties,new Authenticator(){
            @Override protected PasswordAuthentication getPasswordAuthentication(){return new PasswordAuthentication(username,password);}
        });
        try {
            MimeMessage message=new MimeMessage(session);
            message.setFrom(new InternetAddress(from)); message.setRecipient(Message.RecipientType.TO,new InternetAddress(user.getEmail()));
            message.setSubject("Pregunta asignada para revisión: "+q.getNombre(),"UTF-8");
            message.setText("Hola "+user.getName()+",\n\nSe te asignó la revisión de la pregunta "+q.getId()+" - "+q.getNombre()+".\nIngresa al Banco de Preguntas Saber Pro para revisarla.","UTF-8");
            Transport.send(message);
            log("Correo enviado a "+user.getEmail()+" para la pregunta "+q.getId());
        } catch (MessagingException e) {
            throw new IllegalStateException("No se pudo enviar el correo SMTP a "+user.getEmail()+": "+e.getMessage(),e);
        }
    }
    private String required(String key) {
        String value=System.getenv(key);
        if(value==null||value.isBlank()) throw new IllegalStateException("Falta configurar la variable de entorno "+key+" para enviar correos.");
        return value;
    }
    private void log(String line) {
        try { Files.writeString(Path.of("notificaciones.log"),line+System.lineSeparator(),StandardOpenOption.CREATE,StandardOpenOption.APPEND); }
        catch(IOException e) { System.err.println("No se pudo guardar el registro de correo: "+e.getMessage()); }
    }
}
