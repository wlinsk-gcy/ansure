package com.wlinsk.ansure.basic.config.email;

import com.wlinsk.ansure.basic.exception.BasicException;
import com.wlinsk.ansure.basic.exception.SysCode;
import lombok.extern.slf4j.Slf4j;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @Author: wlinsk
 * @Date: 2025/5/22
 */
@Slf4j
public class EmailClient {
    private final Session emailSession;

    public EmailClient(Session emailSession) {
        this.emailSession = emailSession;
    }

    public void sendVerifyCodeEmail(String to, String verifyCode) {
        try {
            Message message = new MimeMessage(emailSession);
            message.setFrom(new InternetAddress("Ansure AI Teams<13421523703@163.com>"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject("Ansure AI 的邮箱验证码");
            message.setContent(verifyTemplate(verifyCode), "text/html;charset=UTF-8");
            long startTime = System.currentTimeMillis();
            Transport.send(message);
            log.info("验证码邮件发送成功：to：{}，耗时：{}s", to, (System.currentTimeMillis() - startTime) / 1000);
        } catch (Exception e) {
            log.error("验证码邮件发送失败：to：{}", to, e);
            throw new BasicException(SysCode.SYSTEM_ERROE);
        }
    }

    private String verifyTemplate(String verifyCode) {
        String template = """
                <div style="font-size: 16px; color: #555555; line-height: 1.6;">
                    亲爱的用户，您好！<br><br>
                    您正在进行身份验证操作，本次验证码为：
                    <div style="font-size: 28px; font-weight: bold; color: #007BFF; text-align: center; margin: 20px 0;">
                        %s
                    </div>
                    请在 <strong>3 分钟内</strong>完成验证。请勿向他人泄露此验证码。<br><br>
                    如果您没有请求此验证码，请忽略此邮件。
                </div>
                """;
        return String.format(template, verifyCode);
    }
}
