package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.MailService;
import ar.edu.itba.paw.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.apache.commons.io.IOUtils;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void sendMail(String to, String subject, String content, boolean containsHTML) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message,true);

            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, containsHTML);
            messageHelper.setFrom("noreplytutv@gmail.com", "Equipo TuTv"); //TODO REPITO EL MAIL, PONERLO COMO CTE?
        } catch (Exception e) {
            //TODO como manejamos esto?
        }

        mailSender.send(message);
    }

    private String getConfirmationMailBody(User u, String confirmationUrl) {
        String mailPath = "/i18n/mail_messages/";
        String mailFile;
        if(LocaleContextHolder.getLocale() == Locale.ENGLISH)
            mailFile = mailPath + "confirmation_en.mail";
        else
            mailFile = mailPath + "confirmation_es.mail";

        InputStream inputStream = getClass()
                .getClassLoader().getResourceAsStream(mailFile);
        try {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8)
                    .replace("VAL_USERNAME", u.getUserName())
                    .replace("VAL_CONFIRMATION_ADDRESS", confirmationUrl);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void sendConfirmationMail(User u, String BaseUrl) {
        String body = getConfirmationMailBody(u, BaseUrl + "/mailconfirm?token=" + u.getConfirmationKey());

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message,true);

            messageHelper.setTo(u.getMailAddress());
            messageHelper.setSubject(messageSource.getMessage("confirmationMail.subject",null, LocaleContextHolder.getLocale()));
            messageHelper.setText(body, true);
            messageHelper.setFrom("noreplytutv@gmail.com", messageSource.getMessage("confirmationMail.from",null, LocaleContextHolder.getLocale())); //TODO REPITO EL MAIL, PONERLO COMO CTE?

            //messageHelper.addInline("dividerImage", new InputStreamResource(getClass().getClassLoader().getResourceAsStream("/media/mail_messages/confirmation_en.mail")));
        } catch (Exception e) {
            //TODO como manejamos esto?
        }

        mailSender.send(message);
    }
}
