package com.alquinow.util; // Asegurate de que sea tu paquete

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {
    
    // Acá están las variables (sin espacios en la clave)
    private static final String REMITENTE = "demiancastro697@gmail.com";
    private static final String CLAVE = "svbkkebyyxhezluf"; 

    public static boolean enviarRecordatorio(String destinatario) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Ahora sí va a reconocer REMITENTE y CLAVE sin problema
                return new PasswordAuthentication(REMITENTE, CLAVE);
            }
        });

        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(REMITENTE));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject("¡Contanos cómo te fue! - AlquiNow");
            
            // Cuerpo del mail en HTML
            String contenidoHtml = "<h2 style='color:#e55e30;'>¡Esperamos que hayas disfrutado tu estadía!</h2>"
                    + "<p>Tu viaje ha terminado y nos encantaría saber tu opinión.</p>"
                    + "<p>Tenés <strong>60 días</strong> para dejar una reseña en la plataforma y ayudar a otros viajeros.</p>"
                    + "<br><a href='http://localhost:8080/mis-reservas.html' style='background:#e55e30;color:white;padding:10px 20px;text-decoration:none;border-radius:5px;'>Dejar mi reseña</a>";
            
            mensaje.setContent(contenidoHtml, "text/html; charset=utf-8");
            Transport.send(mensaje);
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}