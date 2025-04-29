package com.springboot.mail.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.springboot.subscription.entity.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Transactional
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void sendTemplateEmail(String email, String templateName, String subject, Map<String, Object> variables)
            throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Thymeleaf context 설정
        Context context = new Context();
        context.setVariables(variables);

        String htmlContent = templateEngine.process(templateName, context);

        try {
            helper.setFrom("server@email.com", "구독매니아");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("보낸 사람 이름 설정 실패", e);
        }

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        helper.setReplyTo("subsMania-noreply@gmail.com");

        try {
            javaMailSender.send(message);
        } catch (RuntimeException e) {
            throw new BusinessLogicException(ExceptionCode.SEND_MAIL_FAILED);
        }
    }

    // 결제일 알람
    public void sendReminderEmail(String email, List<Subscription> subscriptions) throws MessagingException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("subscriptions", subscriptions);

        String subject = "[구독매니아] 다음 구독 결제일이 다가오고 있어요!";
        String templateName = "reminder"; // reminder.html 템플릿 사용

        sendTemplateEmail(email, templateName, subject, variables);
    }

    //임시 비밀번호
    public void sendTempPassword(String email, String tempPw) throws MessagingException{
        Map<String, Object> variables = new HashMap<>();
        variables.put("tempPw", tempPw);

        String subject = "[구독매니아] 임시 비밀번호 발급";
        String templateName = "temp-password";

        sendTemplateEmail(email, templateName, subject, variables);
    }
}
