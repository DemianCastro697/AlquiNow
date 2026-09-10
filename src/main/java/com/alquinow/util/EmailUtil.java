package com.alquinow.util; // Ajustá el paquete si lo pusiste en otro lado

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {

    // ⚠️ REEMPLAZÁ ESTOS DATOS POR LOS TUYOS
    private static final String MI_CORREO = "demiancastro697@gmail.com"; 
    private static final String MI_PASS = "svbk keby yxhe zluf"; 

    public static void enviarCodigo(String correoDestino, String codigo) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MI_CORREO, MI_PASS);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(MI_CORREO, "Seguridad AlquiNow"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correoDestino));
        message.setSubject("Tu código de verificación de AlquiNow");
        
        // Cuerpo del mail en formato HTML
        String html = "<h2 style='color:#ff5a5f;'>Bienvenido a AlquiNow</h2>"
                    + "<p>Tu código de seguridad para activar la cuenta es:</p>"
                    + "<h1 style='background:#f4f4f4; padding:10px; display:inline-block; border-radius:5px; letter-spacing: 5px;'>" + codigo + "</h1>"
                    + "<p>Si no fuiste vos, ignorá este correo.</p>";
                    
        message.setContent(html, "text/html; charset=utf-8");

        Transport.send(message);
    }
}