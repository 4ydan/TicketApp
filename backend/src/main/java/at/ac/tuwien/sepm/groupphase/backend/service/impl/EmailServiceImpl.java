package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    @Override
    @Async
    public void sendRegistrationConfirm(String to, String email) {
        log.trace("sending mail to " + to);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Verify your account");
            helper.setFrom("verify@ticketline.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }

    @Override
    @Async
    public void sendPasswordReset(String to, String email) {
        log.trace("sending mail to " + to);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Reset your password");
            helper.setFrom("reset@ticketline.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }

    @Override
    public void sendUserPassword(String to, String email) {
        log.trace("sending mail to " + to);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("New user credentials");
            helper.setFrom("new@ticketline.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("failed to send email");
        }
    }

    @Override
    public String buildEmail(String link) {
        log.trace("Building email");

        final Context ctx = new Context();
        ctx.setVariable("link", link);

        return this.templateEngine.process("passwordResetEmail.html", ctx);
    }

    @Override
    public String buildCreatedEmail(String password) {
        log.trace("Building user creation email");

        final Context ctx = new Context();
        ctx.setVariable("password", password);

        return this.templateEngine.process("newUserMail.html", ctx);
    }

}
