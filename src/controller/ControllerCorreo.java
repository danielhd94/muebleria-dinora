package controller;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

public class ControllerCorreo {

    public static void enviarConGMail(String destinatario, String asunto, String cuerpo) {
        // Esto es lo que va delante de @gmail.com en tu cuenta de correo. Es el remitente también.
    String remitente = "muebleriadinorapuntodeventa@gmail.com";  //Para la dirección nomcuenta@gmail.com
    String clave = "ynrmlpszsmaaiudx";

    Properties props = System.getProperties();
    
    props.put("mail.smtp.host", "smtp.gmail.com");  //El servidor SMTP de Google
    props.put("mail.smtp.user", remitente);
    props.put("mail.smtp.clave", clave);    //La clave de la cuenta
    props.put("mail.smtp.auth", "true");    //Usar autenticación mediante usuario y clave
    props.put("mail.smtp.starttls.enable", "true"); //Para conectar de manera segura al servidor SMTP
    props.put("mail.smtp.port", "587"); //El puerto SMTP seguro de Google

    Session session = Session.getDefaultInstance(props);
    MimeMessage message = new MimeMessage(session);

    
    try{
        message.setFrom(new InternetAddress(remitente));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));   //Se podrían añadir varios de la misma manera
        message.setSubject(asunto);
        message.setText(cuerpo);
        Transport transport = session.getTransport("smtp");
        transport.connect("smtp.gmail.com", remitente, clave);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
        JOptionPane.showMessageDialog(null,"Se ha enviado un mensaje a su correo.\nPor favor revise su bandeja de entrada.");
        
    }catch (MessagingException me) {
        me.printStackTrace();   //Si se produce un error
        JOptionPane.showMessageDialog(null,"No se ha podido enviar el mensaje a su correo.\nPor favor verifique su conexión a internet.");
        
    }

    
}
}


/*public boolean enviarCorreo(Correo c) throws MessagingException{
        try {
            Properties p = new Properties();
            p.put("mail.smtp.host","smt.gmail.com");
            p.setProperty("mail.smtp.starttls.enable","true");
            p.setProperty("mail.smtp.port","587");
            p.setProperty("mail.smtp.user", c.getUsuarioCorreo());
            p.setProperty("mail.smtp.auth", "true");
            
            Session s = Session.getDefaultInstance(p,null);
            BodyPart texto = new MimeBodyPart();
            texto.setText(c.getMensaje());
            
            MimeMessage mensaje = new MimeMessage(s);
            mensaje.setFrom(new InternetAddress(c.getUsuarioCorreo()));
            mensaje.addRecipient(Message.RecipientType.TO, new InternetAddress(c.getDestino()));
            mensaje.setSubject(c.getAsunto());
            
            MimeMultipart m = new MimeMultipart();
            m.addBodyPart(texto);
            mensaje.setContent(m);
            Transport t = s.getTransport("smtp");
            t.connect(c.getUsuarioCorreo(),c.getPassword());
            t.sendMessage(mensaje, mensaje.getAllRecipients());
            t.close();
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Ha ocurrido un erro: "+e);
            return false;
        }
        
    }*/

