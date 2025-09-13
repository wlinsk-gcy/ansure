package com.wlinsk.ansure.basic.config.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

/**
 * @Author: wlinsk
 * @Date: 2025/5/22
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "email",name = {"smtpCode", "smtpEmail","host","port"})
public class EmailAutoConfiguration {

    @Value("${email.host}")
    private String host;
    @Value("${email.port}")
    private String port;
    @Value("${email.smtpCode}")
    private String smtpCode;
    @Value("${email.smtpEmail}")
    private String smtpEmail;

    //    @Bean
    public Session emailSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpEmail, smtpCode);
            }
        });
    }

    @Bean
    public EmailClient emailClient() {
        log.info("Email Client initialized...");
        return new EmailClient(emailSession());
    }
}
