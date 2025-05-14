package com.eureka.mp2.team4.planit.common.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.eureka.mp2.team4.planit.common.email.MailTemplates.PASSWORD_RESET_LINK;
import static com.eureka.mp2.team4.planit.common.email.MailTemplates.PASSWORD_RESET_SUBJECT;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void sendPasswordResetEmail(String to, String token) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(PASSWORD_RESET_SUBJECT);

            String htmlBody = MailTemplates.passwordReset(PASSWORD_RESET_LINK + token);
            helper.setText(htmlBody, true);

            javaMailSender.send(mimeMessage);
            log.info("이메일 전송 성공 :{}", to);
        } catch (Exception e) {
            log.info("이메일 전송 실패 :{}", to);
        }
    }
}
