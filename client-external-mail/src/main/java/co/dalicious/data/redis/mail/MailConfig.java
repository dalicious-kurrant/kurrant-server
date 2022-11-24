package co.dalicious.data.redis.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@PropertySource("classpath:application-mail.properties")
public class MailConfig {
        @Value("${spring.mail.username}")
        private String id;
        @Value("${spring.mail.password}")
        private String password;
        @Value("${spring.mail.host}")
        private String host;
        @Value("${spring.mail.port}")
        private int port;

        @Bean
        public JavaMailSender javaMailService() {
            JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(host);
            mailSender.setPort(port);

            mailSender.setUsername(id);
            mailSender.setPassword(password);

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "true");

            return mailSender;
        }

}
