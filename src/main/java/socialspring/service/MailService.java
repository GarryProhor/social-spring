package socialspring.service;

public interface MailService {
    void sendEmail(String toAddress, String subject, String content) throws Exception;
}
