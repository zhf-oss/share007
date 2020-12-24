package com.zhf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * 邮件配置类
 * @author Administrator
 *
 */
@Configuration
public class MailConfig {

	/**
	 * 获取邮件发送实例
	 * @return
	 */
	@Bean
	public MailSender mailSender(){
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.qq.com");//指定用来发送Email的邮件服务器主机名
        mailSender.setPort(587);//默认端口，标准的SMTP端口
        mailSender.setUsername("862592396@qq.com");//用户名
        mailSender.setPassword("mplybaztkgznbdec");//密码
        return mailSender;
	}
}
