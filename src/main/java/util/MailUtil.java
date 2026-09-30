package util;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class MailUtil {

    public static void sendMail(String to, String from, String subject, String body, boolean bodyIsHTML) 
            throws MessagingException {
        
        // 1. Cấu hình các thuộc tính kết nối SMTP (Ví dụ dùng Gmail)
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        // Thêm timeout để tránh treo request khi host block SMTP (đơn vị: ms)
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        // 2. Tạo Authenticator để xác thực tài khoản gửi
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Thay thế bằng Email thực tế
                return new PasswordAuthentication("legiahanst2006@gmail.com", "xzza jayg dshp goht");
            }
        };

        // 3. Khởi tạo phiên làm việc Session
        Session session = Session.getInstance(props, auth);
        // session.setDebug(true); // Tắt debug trên production

        // 4. Tạo đối tượng MimeMessage chứa nội dung email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        
        if (bodyIsHTML) {
            message.setContent(body, "text/html; charset=utf-8");
        } else {
            message.setText(body);
        }

        // 5. Gửi email thông qua Transport
        Transport.send(message);
    }
}