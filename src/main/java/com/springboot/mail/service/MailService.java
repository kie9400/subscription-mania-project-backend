package com.springboot.mail.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Transactional
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private static final String TITLE = "[구독매니아] 이메일 인증 코드";

    public void sendEmail(String email, String code) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Thymeleaf context 설정
        Context context = new Context();
        context.setVariable("code", code);

        // 템플릿 렌더링
        String htmlContent = templateEngine.process("email", context);

        helper.setTo(email);
        helper.setSubject(TITLE);
        helper.setText(htmlContent, true);
        helper.setReplyTo("subsMania-noreply@gmail.com"); //회신 불가능한 주소로 설정
        try {
            javaMailSender.send(message);
        } catch (RuntimeException e) {
            throw new BusinessLogicException(ExceptionCode.SEND_MAIL_FAILED);
        }
    }
}
