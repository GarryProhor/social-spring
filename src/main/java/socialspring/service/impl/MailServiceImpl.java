package socialspring.service.impl;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;
import socialspring.exception.EmailFailedToSendException;
import socialspring.service.MailService;


import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

@Service
public class MailServiceImpl implements MailService {

    private final Gmail gmail;

    public MailServiceImpl(Gmail gmail) {
        this.gmail = gmail;
    }

    @Override
    public void sendEmail(String toAddress, String subject, String content) throws EmailFailedToSendException{
        Properties props = new Properties();

        Session session = Session.getInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        try {
            email.setFrom(new InternetAddress("belprofmash.gomel@gmail.com"));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toAddress));
            email.setSubject(subject);
            email.setText(content);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            email.writeTo(buffer);

            byte[] rawMessageBytes = buffer.toByteArray();

            String encoderEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);

            Message message = new Message();
            message.setRaw(encoderEmail);

            message = gmail.users().messages().send("me", message).execute();
        }catch (Exception e){
            throw new EmailFailedToSendException();
        }
    }
}
