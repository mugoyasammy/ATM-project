package atm_sinulation;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
public class Mailing {
public static void sendEmail(String reciprient,String subject,String content) {
	String username= "mugoyamuwanga@gmail.com";
	String password = "kcji umcg vbpp cjhz";
	Properties prop = new Properties();
	prop.put("mail.smtp.auth","true");
	prop.put("mail.smtp.starttls.enable","true");
	prop.put("mail.smtp.host", "smtp.gmail.com");
	prop.put("mail.smtp.port", "587");
	
	
	Session session = Session.getInstance(prop,new Authenticator() {
		protected PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(username,password);
		}
	});
	
	Message message = new MimeMessage(session);
	try {
		message.setFrom(new InternetAddress(username));
		message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(reciprient) );
		message.setSubject(subject);
		message.setText(content);
		Transport.send(message);
		System.out.println("Email has been sent Successfully");
	} 
	catch (MessagingException e) {
		
		e.printStackTrace();
	}
}
}
