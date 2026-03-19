package org.xiaoyu.xchatmind.service.Impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.xiaoyu.xchatmind.service.EmailService;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendEmailAsync(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);

            log.info("异步发送邮件成功，收件人: {}, 主题: {}", to, subject);
        } catch (Exception e) {
            log.error("异步发送邮件失败，收件人: {}, 主题: {}, 错误: {}", to, subject, e.getMessage(), e);
        }
    }
}
