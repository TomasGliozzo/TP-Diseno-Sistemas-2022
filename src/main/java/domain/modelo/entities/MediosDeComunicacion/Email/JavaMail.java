package domain.modelo.entities.MediosDeComunicacion.Email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaMail implements AdapterServicioEmail{
    @Override
    public void enviarMensaje(String destinatario, String cuerpo) {
        Properties properties = new Properties();

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        String miCuentaDeEmail = "pruebajavamailDDS@gmail.com";
        String contrasenia = "pjuvyvvzunptkezi";

        Session sesion = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(miCuentaDeEmail, contrasenia);
            }
        });

        Message mail = prepararMail(sesion, miCuentaDeEmail, destinatario, cuerpo);
        try {
            Transport.send(mail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static Message prepararMail(Session sesion, String miCuentaDeEmail, String destinatario, String cuerpo) {
        try {
            Message mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(miCuentaDeEmail));
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            mensaje.setSubject("Aca va el asunto");
            mensaje.setText(cuerpo);
            return mensaje;
        } catch (Exception ex) {
            Logger.getLogger(JavaMail.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
