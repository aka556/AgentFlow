package org.xiaoyu.xchatmind.service;

public interface EmailService {
    void sendEmailAsync(String to, String subject, String content);
}
